package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.Enums.NotificationType;

public class EmailTemplateResolver {

    public static String resolve(NotificationType type) {
        return switch (type) {
            case USER_WELCOME -> "templates/email/welcome-user.vm";
            case MONTHLY_SUMMARY -> "templates/email/monthly-summary.vm";
            case PASSWORD_RESET -> "templates/email/password-reset.vm";
        };
    }
}

