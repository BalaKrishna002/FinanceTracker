package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.AggregationResponseDTO;
import com.financeTracker.financeTracker.DTO.NotificationRequest;
import com.financeTracker.financeTracker.Enums.AggregationMode;
import com.financeTracker.financeTracker.Enums.NotificationChannelType;
import com.financeTracker.financeTracker.Enums.NotificationType;
import com.financeTracker.financeTracker.model.User;
import com.financeTracker.financeTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlySummaryScheduler {

    private final UserRepository userRepository;
    private final TransactionServiceImpl transactionService;
    private final AsyncNotificationService asyncNotificationService;

    /**
     * Runs every 15 minutes between day 1-5 of each month
     */
    @Scheduled(cron = "* */2 * * * *")
    public void sendMonthlySummary() {

        for (User user : userRepository.findAll()) {
            try {
                processUser(user);
            } catch (Exception e) {
                log.error("Monthly summary failed for user {}", user.getUsername(), e);
            }
        }
    }

    private void processUser(User user) {

        ZoneId zoneId = ZoneId.of(user.getTimezone());
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Send only on 2nd day at 09:30 AM
//        if (now.getDayOfMonth() != 2) return;
//        if (now.getHour() != 9 || now.getMinute() != 30) return;

        ZonedDateTime from = now.minusMonths(1)
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay(zoneId);

        ZonedDateTime to = now.minusMonths(1)
                .withDayOfMonth(now.minusMonths(1).toLocalDate().lengthOfMonth())
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(zoneId);

        var data = transactionService.aggregateForUser(
                user.getId(),
                zoneId,
                AggregationMode.MONTH,
                from.toInstant(),
                to.toInstant()
        );

        var totalCredit = data.stream()
                .map(AggregationResponseDTO::getTotalCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalDebit = data.stream()
                .map(AggregationResponseDTO::getTotalDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ ASYNC WELCOME EMAIL (NON-BLOCKING)
        asyncNotificationService.send(
                NotificationRequest.builder()
                        .channelType(NotificationChannelType.EMAIL)
                        .notificationType(NotificationType.MONTHLY_SUMMARY)
                        .to(user.getUsername())
                        .subject("Your Monthly Finance Summary")
                        .data(Map.of(
                                "fullName", user.getFullName(),
                                "totalCredit", totalCredit,
                                "totalDebit", totalDebit,
                                "year", Year.now().getValue()
                        ))
                        .build()
        );
    }
}

