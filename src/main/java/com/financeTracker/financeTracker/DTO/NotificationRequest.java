package com.financeTracker.financeTracker.DTO;

import com.financeTracker.financeTracker.Enums.NotificationChannelType;
import com.financeTracker.financeTracker.Enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationRequest {

    private NotificationChannelType channelType;
    private NotificationType notificationType;

    // Email specific
    private String to;
    private String subject;

    // Template data
    private Map<String, Object> data;
}

