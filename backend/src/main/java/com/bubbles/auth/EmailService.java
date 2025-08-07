package com.bubbles.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String from;
    private final String frontendBaseUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${MAIL_FROM:noreply@bubbles.app}") String from,
                        @Value("${FRONTEND_BASE_URL:http://localhost:3000}") String frontendBaseUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void send(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            // Fallback to plain text if HTML fails
            send(to, subject, htmlBody.replaceAll("<[^>]+>", ""));
        }
    }

    public String buildVerificationEmailHtml(String token) {
        String url = frontendBaseUrl + "/auth/verify?token=" + token;
        return "<div style=\"font-family:system-ui,Segoe UI,Arial\">" +
                "<h2>Verify your email</h2>" +
                "<p>Please confirm your email to start using Bubbles.</p>" +
                "<p><a href=\"" + url + "\" style=\"display:inline-block;padding:10px 16px;background:#2563eb;color:white;text-decoration:none;border-radius:6px\">Verify Email</a></p>" +
                "<p>If the button doesn't work, copy this code:</p>" +
                "<pre style=\"background:#f3f4f6;padding:8px;border-radius:6px\">" + token + "</pre>" +
                "</div>";
    }
}


