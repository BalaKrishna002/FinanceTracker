package com.financeTracker.financeTracker.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregationResponseDTO {

    // Start of week / month (UTC)
    private Instant periodStart;

    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
}
