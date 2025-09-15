package org.fastcampus.jober.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomMailSender {

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    public void sendMail(String to, String url, String subject, String templatePath, String plainText) throws MessagingException {
        Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("resetUrl", url);

        String html = templateEngine.process(templatePath, ctx);

        MimeMessage mime = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setText(plainText, html);

        javaMailSender.send(mime);
    }

    public void sendPasswordResetMail(final String email, final String resetUrl, final String plain) throws MessagingException {
        this.sendMail(
                email,
                resetUrl,
                "[Jober] 비밀번호 재설정",
                "mail/password-reset",
                plain
        );

    }
}
