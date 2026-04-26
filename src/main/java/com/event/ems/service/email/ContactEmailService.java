package com.event.ems.service.email;

import com.event.ems.dto.ContactEmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("CONTACT")
public class ContactEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(Object request) {
        ContactEmailRequest req = (ContactEmailRequest) request;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("dilushamadushan007@gmail.com");
            helper.setSubject("New Inquiry: " + req.getSubject());
            helper.setFrom(req.getFrom());

            String htmlContent = "<h2>New Inquiry Received</h2>"
                    + "<p><b>Name:</b> " + req.getName() + "</p>"
                    + "<p><b>Email:</b> " + req.getFrom() + "</p>"
                    + "<p><b>Subject:</b> " + req.getSubject() + "</p>"
                    + "<p><b>Message:</b><br>" + req.getBody() + "</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Contact email failed", e);
        }
    }
}