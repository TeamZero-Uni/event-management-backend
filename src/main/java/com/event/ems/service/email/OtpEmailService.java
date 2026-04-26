package com.event.ems.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("OTP")
public class OtpEmailService implements EmailService{

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
            helper.setSubject("Password Reset OTP");

            String html = "<h3>Your OTP Code</h3>"
                    + "<h2>" + data.get("otp") + "</h2>"
                    + "<p>This OTP expires in 5 minutes</p>";

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("OTP email failed", e);
        }
    }
}
