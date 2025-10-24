package com.dev.TomoAdministration.util;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.dto.MailSendResult;


@Service
public class CsvMailLogger {

    /** 기본값도 D:/tomo/log 로 설정 */
    private final String baseDir;

    public CsvMailLogger(@Value("${spring.mail.log.dir:D:/tomo/log}") String baseDir) {
        // 역슬래시로 입력해도 문제없도록 일괄 정규화
        this.baseDir = baseDir.replace('\\', '/');
    }

    /** 실행 1회분 결과를 CSV로 저장하고 파일 경로를 반환 */
    public Path writeRunCsv(List<MailSendResult> results) {
        try {
            // 날짜별 하위 폴더(선택): D:/tomo/log/2025-10-24
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            Path dir = Paths.get(baseDir, dateFolder);

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path file = dir.resolve("send-result-" + ts + ".csv");

            try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE),
                    StandardCharsets.UTF_8))) {

                // ✅ UTF-8 BOM (Excel 문자 깨짐 방지)
                w.write('\uFEFF');

                // Header
                w.write("username,email,success,error,sentAt");
                w.newLine();

                // Rows
                for (MailSendResult r : results) {
                    w.write(csv(r.getUsername())); w.write(',');
                    w.write(csv(r.getEmail()));    w.write(',');
                    w.write(r.isSuccess() ? "true" : "false"); w.write(',');
                    w.write(csv(r.getError()));    w.write(',');
                    w.write(csv(r.getSentAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                    w.newLine();
                }
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException("CSV 로그 저장 실패", e);
        }
    }

    /** CSV 필드 이스케이프 */
    private String csv(String s) {
        if (s == null) return "";
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String val = s.replace("\"", "\"\"");
        return needQuote ? "\"" + val + "\"" : val;
    }
}