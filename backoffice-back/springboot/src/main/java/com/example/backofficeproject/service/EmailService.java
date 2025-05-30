package com.example.backofficeproject.service;


import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendActivationEmail(String toEmail, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configure sender/recipient
            helper.setFrom(fromEmail, "Ooredoo Admin"); // Will show as "Ooredoo Admin <melikbensalah2003@gmail.com>"
            helper.setTo(toEmail);
            helper.setSubject("Your Ooredoo Account Activation");

            // Build HTML content with logo
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px;">
                    <div style="background-color: #E30613; padding: 20px; text-align: center;">
                        <h2 style="color: white; margin: 0;">Ooredoo Account Activated</h2>
                    </div>
                    <div style="padding: 30px;">
                        <p>Dear User,</p>
                        <p>Your account has been activated with the role: <strong>%s</strong></p>
                        <p>You can now access the platform using your existing credentials:</p>
                        <p>Email: <strong>%s</strong></p>
                        <div style="margin-top: 30px; text-align: center;">
                            <img src="backoffice-back/user-management-auth-service/src/main/resources/static/logo_ooredoo.png" 
                                 alt="Ooredoo Logo" 
                                 style="height: 50px;">
                        </div>
                    </div>
                </div>
                """.formatted(role, toEmail);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            logger.info("Activation email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", toEmail, e);
            throw new RuntimeException("Email sending failed");
        }
    }
}