package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.NotificationRequest;
import com.financeTracker.financeTracker.Enums.NotificationChannelType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final Map<NotificationChannelType, NotificationChannel> channelMap;

    public NotificationService(List<NotificationChannel> channels) {
        this.channelMap = channels.stream()
                .collect(Collectors.toMap(
                        NotificationChannel::getType,
                        Function.identity()
                ));
    }

    public void send(NotificationRequest request) {
        NotificationChannel channel = channelMap.get(request.getChannelType());

        if (channel == null) {
            throw new IllegalArgumentException("Unsupported channel: " + request.getChannelType());
        }

        channel.send(request);
    }
}

