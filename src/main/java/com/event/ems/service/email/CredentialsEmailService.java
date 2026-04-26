package com.event.ems.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("CREDENTIALS")
public class CredentialsEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(Object request) {
        Map<String, String> data = (Map<String, String>) request;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("dilushamadushan007@gmail.com");
            helper.setTo(data.get("email"));
            helper.setSubject("Your Account Details");

            String html = "<h3>Welcome to EMS 🎉</h3>"
                    + "<p>Your account has been created successfully.</p>"
                    + "<p><b>Username:</b> " + data.get("username") + "</p>"
                    + "<p><b>Password:</b> " + data.get("password") + "</p>"
                    + "<br><p>Please change your password after login.</p>";

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Credentials email failed", e);
        }
    }
}