package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.NotificationRequest;
import com.financeTracker.financeTracker.Enums.NotificationChannelType;

public interface NotificationChannel {

    NotificationChannelType getType();

    void send(NotificationRequest request);
}

