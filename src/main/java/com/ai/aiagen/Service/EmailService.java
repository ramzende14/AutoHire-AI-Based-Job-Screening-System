package com.ai.aiagen.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Shortlist Mail
    public void sendShortlistMail(String to, String username, int score) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🎉 Congratulations! You are Shortlisted");

            String htmlContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; padding: 20px;">
                            <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0px 4px 8px rgba(0,0,0,0.1);">
                                <h2 style="color: #2E86C1;">Hello %s,</h2>
                                <p>We analyzed your resume and your score is 
                                   <b style="color:green;">%d / 100</b>.
                                </p>
                                <p style="color: #27AE60; font-size: 16px;">
                                   ✅ You are shortlisted for the next round of selection!
                                </p>
                                <br/>
                                <p style="font-size: 14px; color: #555;">Best Regards,<br/>
                                <b>AI Hiring Team</b></p>
                            </div>
                        </body>
                    </html>
                    """.formatted(username, score);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send shortlist mail", e);
        }
    }

    // ✅ Rejection Mail
    public void sendRejectionMail(String to, String username, int score) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("❌ Application Update - Thank You for Applying");

            String htmlContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; padding: 20px;">
                            <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0px 4px 8px rgba(0,0,0,0.1);">
                                <h2 style="color: #C0392B;">Hello %s,</h2>
                                <p>We analyzed your resume and your score is 
                                   <b style="color:red;">%d / 100</b>.
                                </p>
                                <p style="color: #E74C3C; font-size: 16px;">
                                   ❌ Unfortunately, we will not be moving forward with your application at this time.
                                </p>
                                <p style="font-size: 14px; color: #555;">
                                   We truly appreciate your interest and encourage you to apply again in the future.
                                </p>
                                <br/>
                                <p style="font-size: 14px; color: #555;">Best Regards,<br/>
                                <b>AI Hiring Team</b></p>
                            </div>
                        </body>
                    </html>
                    """.formatted(username, score);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send rejection mail", e);
        }
    }
}
