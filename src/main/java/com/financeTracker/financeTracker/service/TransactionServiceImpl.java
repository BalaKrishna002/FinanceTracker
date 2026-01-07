package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.AggregationResponseDTO;
import com.financeTracker.financeTracker.DTO.TransactionDTO;
import com.financeTracker.financeTracker.Enums.AggregationMode;
import com.financeTracker.financeTracker.model.Transaction;
import com.financeTracker.financeTracker.model.User;
import com.financeTracker.financeTracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    // =========================
    // API METHODS (UNCHANGED)
    // =========================

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactions(Long fromEpoch, Long toEpoch) {

        User user = userService.getCurrentUser();
        ZoneId zoneId = ZoneId.of(user.getTimezone());

        Instant from;
        Instant to;

        if (fromEpoch != null && toEpoch != null) {
            if (fromEpoch > toEpoch) {
                throw new IllegalArgumentException("from cannot be greater than to");
            }
            from = Instant.ofEpochMilli(fromEpoch);
            to = Instant.ofEpochMilli(toEpoch);
        } else {
            to = Instant.now();
            from = to.minus(365, ChronoUnit.DAYS);
        }

        return transactionRepository
                .findByUserAndCreatedAtBetween(user, from, to)
                .stream()
                .map(tx -> toDTOWithUserTimezone(tx, zoneId))
                .toList();
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return toDTO(transaction);
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Transaction transaction = toEntity(dto);
        Transaction saved = transactionRepository.save(transaction);
        return toDTO(saved);
    }

    @Override
    public List<TransactionDTO> createTransactions(List<TransactionDTO> dtos) {

        List<Transaction> entities = dtos.stream()
                .map(this::toEntity)
                .toList();

        List<Transaction> savedTransactions =
                transactionRepository.saveAll(entities);

        return savedTransactions.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void deleteTransactionById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // =========================
    // AGGREGATION (API)
    // =========================

    @Override
    public List<AggregationResponseDTO> aggregate(
            AggregationMode mode,
            Long fromEpoch,
            Long toEpoch) {

        User user = userService.getCurrentUser();
        ZoneId zoneId = ZoneId.of(user.getTimezone());

        return aggregateForUser(
                user.getId(),
                zoneId,
                mode,
                Instant.ofEpochMilli(fromEpoch),
                Instant.ofEpochMilli(toEpoch)
        );
    }

    // =========================
    // AGGREGATION (CORE LOGIC)
    // =========================

    /**
     * 🔹 Reusable aggregation logic
     * Used by:
     * - API
     * - Scheduler (monthly summary email)
     * - Future reports
     */
    public List<AggregationResponseDTO> aggregateForUser(
            Long userId,
            ZoneId zoneId,
            AggregationMode mode,
            Instant from,
            Instant to
    ) {

        List<Object[]> rows = switch (mode) {
            case MONTH -> transactionRepository.aggregateByMonth(userId, from, to);
            case YEAR -> transactionRepository.aggregateByYear(userId, from, to);
        };

        return rows.stream()
                .map(row -> {
                    Instant utcInstant = (Instant) row[0];

                    return AggregationResponseDTO.builder()
                            .periodStart(
                                    utcInstant.atZone(ZoneOffset.UTC)
                                            .withZoneSameInstant(zoneId)
                                            .toInstant()
                            )
                            .totalCredit((BigDecimal) row[1])
                            .totalDebit((BigDecimal) row[2])
                            .build();
                })
                .toList();
    }

    // =========================
    // MAPPER METHODS
    // =========================

    private TransactionDTO toDTO(Transaction entity) {
        return TransactionDTO.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .transactionType(entity.getTransactionType())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Transaction toEntity(TransactionDTO dto) {

        if (dto.getCreatedAt() == null) {
            dto.setCreatedAt(Instant.now());
        }

        return Transaction.builder()
                .amount(dto.getAmount())
                .transactionType(dto.getTransactionType())
                .description(dto.getDescription())
                .user(userService.getCurrentUser())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    private TransactionDTO toDTOWithUserTimezone(Transaction tx, ZoneId zoneId) {

        return TransactionDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .transactionType(tx.getTransactionType())
                .description(tx.getDescription())
                .createdAt(
                        tx.getCreatedAt()
                                .atZone(ZoneOffset.UTC)
                                .withZoneSameInstant(zoneId)
                                .toInstant()
                )
                .updatedAt(
                        tx.getUpdatedAt()
                                .atZone(ZoneOffset.UTC)
                                .withZoneSameInstant(zoneId)
                                .toInstant()
                )
                .build();
    }
}