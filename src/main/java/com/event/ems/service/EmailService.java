package com.event.ems.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendContactEmail(String name, String from, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("dilushamadushan007@gmail.com");
            helper.setSubject("New Inquiry: " + subject);
            helper.setFrom(from);

            String htmlContent = "<h2>New Inquiry Received</h2>"
                    + "<p><b>Name:</b> " + name + "</p>"
                    + "<p><b>Email:</b> " + from + "</p>"
                    + "<p><b>Subject:</b> " + subject + "</p>"
                    + "<p><b>Message:</b><br>" + body + "</p>"
                    + "<hr>"
                    + "<p style='color:gray;'>Event Management System</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}