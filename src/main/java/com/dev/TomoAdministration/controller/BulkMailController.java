package com.dev.TomoAdministration.controller;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.TomoAdministration.dto.MailSendResult;
import com.dev.TomoAdministration.service.MimeEmailService;
import com.dev.TomoAdministration.util.CsvMailLogger;
import com.dev.TomoAdministration.util.ExcelReader;
import com.dev.TomoAdministration.util.MailTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class BulkMailController {

    private final MimeEmailService mimeEmailService;
    private final CsvMailLogger csvMailLogger;

    @Value("${spring.mail.throttle-ms:200}")
    private long throttleMs; // 기본 200ms

    @PostMapping(value = "/send-excel", consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendFromExcel(@RequestParam("file") MultipartFile file) {
        List<MailSendResult> results = new ArrayList<>();
        try {
            List<ExcelReader.RowData> rows = ExcelReader.readUsernameEmail(file);
            if (rows.isEmpty()) {
                return ResponseEntity.badRequest().body("엑셀에서 (username,email) 데이터를 찾지 못했습니다.");
            }

            String subject = MailTemplate.subject();

            int success = 0, fail = 0;

            for (int i = 0; i < rows.size(); i++) {
                ExcelReader.RowData row = rows.get(i);
                String email = row.getEmail();
                String username = row.getUsername();

                try {
                    String html = MailTemplate.buildHtml(username);
                    mimeEmailService.sendInlineImageHtml(
                            email,
                            subject,
                            html,
                            "static/mail/mail.png", // classpath 경로
                            "banner"
                    );
                    results.add(new MailSendResult(username, email, true, "", LocalDateTime.now()));
                    success++;
                } catch (Exception e) {
                    String errMsg = e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "" : e.getMessage());
                    results.add(new MailSendResult(username, email, false, errMsg, LocalDateTime.now()));
                    log.error("수신자 {} 메일 발송 실패", email, e);
                    fail++;
                }

                // ✅ 건당 지연 (스로틀)
                if (throttleMs > 0 && i < rows.size() - 1) {
                    try {
                        Thread.sleep(throttleMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("메일 스로틀링 중 인터럽트 발생");
                    }
                }
            }

            // ✅ CSV 저장
            Path csvPath = csvMailLogger.writeRunCsv(results);
            String msg = String.format("메일 발송 완료 - 성공:%d, 실패:%d, CSV: %s", success, fail, csvPath.toAbsolutePath());
            return ResponseEntity.ok(msg);

        } catch (Exception e) {
            // 전체 실패도 CSV로 남김
            results.add(new MailSendResult("", "", false, "엑셀 처리 실패: " + e.getMessage(), LocalDateTime.now()));
            try {
                Path csvPath = csvMailLogger.writeRunCsv(results);
                log.error("엑셀 처리 실패. CSV: {}", csvPath.toAbsolutePath(), e);
                return ResponseEntity.internalServerError().body("엑셀 처리 실패: " + e.getMessage() + " (CSV: " + csvPath.toAbsolutePath() + ")");
            } catch (Exception logErr) {
                log.error("엑셀 처리 실패 및 CSV 저장도 실패", logErr);
                return ResponseEntity.internalServerError().body("엑셀 처리 실패: " + e.getMessage());
            }
        }
    }
}