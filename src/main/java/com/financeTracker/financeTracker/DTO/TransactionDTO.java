package com.financeTracker.financeTracker.DTO;
import com.financeTracker.financeTracker.Enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}

