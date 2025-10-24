package com.dev.TomoAdministration.service;

import javax.mail.internet.MimeMessage;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MimeEmailService {

    private final JavaMailSender mailSender;

    /**
     * 인라인 이미지가 포함된 HTML 메일 발송
     * @param to 수신자
     * @param subject 제목
     * @param htmlBody HTML 본문 (cid:banner 같은 CID 참조 포함)
     * @param classpathImage 리소스 경로 예) "static/mail/mail.png"
     * @param contentId CID 예) "banner"
     */
    public void sendInlineImageHtml(
            @NonNull String to,
            @NonNull String subject,
            @NonNull String htmlBody,
            @NonNull String classpathImage,
            @NonNull String contentId
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = multipart
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML
            // 보내는 사람을 명시하고 싶다면 아래 주석 해제
            // helper.setFrom("admin@qlixjp.co.jp", "SNSTOMO運営事務局");

            ClassPathResource image = new ClassPathResource(classpathImage);
            if (!image.exists()) {
                throw new IllegalStateException("이미지 리소스를 찾을 수 없습니다: " + classpathImage);
            }
            helper.addInline(contentId, image, "image/png");

            mailSender.send(message);
            log.info("[MAIL] {} 로 발송 완료", to);
        } catch (Exception e) {
            log.error("[MAIL] 발송 실패: {}", to, e);
            throw new RuntimeException("메일 발송 실패: " + to, e);
        }
    }
}