package com.event.ems.service.email;

import com.event.ems.dto.ConformRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("REGISTRATION")
public class RegistrationEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(Object request) {
        ConformRequest req = (ConformRequest) request;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(req.getStudentEmail());
            helper.setSubject("Event Registration Confirmation");
            helper.setFrom("dilushamadushan007@gmail.com");

            String htmlContent =
                    "<h2>Registration Confirmed 🎉</h2>"
                            + "<p>Dear " + req.getStudentName() + ",</p>"
                            + "<p>You have successfully registered for the event.</p>"
                            + "<p><b>Event:</b> " + req.getEventTitle() + "</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Registration email failed", e);
        }
    }
}