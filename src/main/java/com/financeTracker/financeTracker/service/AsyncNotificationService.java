package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.NotificationRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncNotificationService {

    private final NotificationService notificationService;

    public AsyncNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    public void send(NotificationRequest request) {
        notificationService.send(request);
    }
}

