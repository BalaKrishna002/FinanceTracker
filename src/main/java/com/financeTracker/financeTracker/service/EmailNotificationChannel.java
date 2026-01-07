package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.NotificationRequest;
import com.financeTracker.financeTracker.Enums.NotificationChannelType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailNotificationChannel implements NotificationChannel {

    private final EmailService emailService;

    public EmailNotificationChannel(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {
        String templatePath = EmailTemplateResolver.resolve(request.getNotificationType());

        emailService.send(
                request.getTo(),
                request.getSubject(),
                templatePath,
                request.getData()
        );
    }
}

