package com.financeTracker.financeTracker.service;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Map;

@Service
public class VelocityEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final VelocityEngine velocityEngine;

    public VelocityEmailService(JavaMailSender mailSender, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
    }

    @Override
    public void send(String to, String subject, String templatePath, Map<String, Object> data) {
        try {
            VelocityContext context = new VelocityContext(data);
            StringWriter writer = new StringWriter();
            velocityEngine.mergeTemplate(templatePath, "UTF-8", context, writer);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(writer.toString(), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

