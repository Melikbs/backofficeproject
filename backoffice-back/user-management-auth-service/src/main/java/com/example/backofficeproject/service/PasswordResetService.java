package com.example.backofficeproject.service;


import com.example.backofficeproject.Exceptions.ExpiredTokenException;
import com.example.backofficeproject.Exceptions.InvalidTokenException;
import com.example.backofficeproject.Exceptions.WeakPasswordException;
import com.example.backofficeproject.Helpers.PasswordValidator;
import com.example.backofficeproject.model.PasswordResetToken;
import com.example.backofficeproject.model.Users;
import com.example.backofficeproject.repositories.PasswordResetTokenRepo;
import com.example.backofficeproject.repositories.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;


import java.time.Instant;


import java.time.temporal.ChronoUnit;
import java.util.Date;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
@EnableScheduling
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepo userRepo;
    private final PasswordResetTokenRepo tokenRepo;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Value("${app.reset-password.expiration-minutes}")
    private int expirationMinutes;

    @Value("${app.reset-password.base-url}")
    private String baseUrl;

    public void createPasswordResetToken(String email) {
        Users user = userRepo.findByEmail(email);

        if (user == null) {  // Directly check for null
            throw new RuntimeException("User not found");
        }

        invalidateExistingTokens(user);

        String token = generateUniqueToken();
        PasswordResetToken resetToken = createTokenEntity(user, token);
        tokenRepo.save(resetToken);
        sendResetEmail(user.getEmail(), token);
    }


    private void invalidateExistingTokens(Users user) {
        tokenRepo.deleteByUser(user);
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (tokenRepo.existsByToken(token));
        return token;
    }

    private PasswordResetToken createTokenEntity(Users user, String token) {
        return new PasswordResetToken(user,token,
                Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES))
        );
    }

    private void sendResetEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText(buildEmailContent(token), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send reset email to {}", email, e);
            throw new RuntimeException("Failed to send reset email");
        }
    }

    private String buildEmailContent(String token) {
        return String.format(
                "<p>Click the link to reset your password: " +
                        "<a href=\"%s/reset-password?token=%s\">Reset Password</a></p>",
                baseUrl, token
        );
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        validateTokenExpiration(resetToken);
        validatePasswordStrength(newPassword);

        Users user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        tokenRepo.deleteById(resetToken.getId()); // Safe delete
    }

    private void validateTokenExpiration(PasswordResetToken token) {
        if (token.getExpiryDate().before(new Date())) {
            throw new ExpiredTokenException("Password reset token has expired");
        }
    }

    private void validatePasswordStrength(String password) {
        if (!passwordValidator.isValid(password)) {
            throw new WeakPasswordException("Password does not meet strength requirements");
        }
    }
    @Scheduled(cron = "0 0 0 * * ?") // Daily cleanup at midnight
    public void cleanUpExpiredTokens() {
        tokenRepo.deleteExpiredOrUsedTokens();
    }
}
