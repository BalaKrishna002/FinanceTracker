package com.financeTracker.financeTracker.service;

import java.util.Map;

public interface EmailService {
    void send(String to, String subject, String templatePath, Map<String, Object> data);
}

