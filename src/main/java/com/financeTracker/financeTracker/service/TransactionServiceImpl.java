package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.AggregationResponseDTO;
import com.financeTracker.financeTracker.DTO.TransactionDTO;
import com.financeTracker.financeTracker.Enums.AggregationMode;
import com.financeTracker.financeTracker.model.Transaction;
import com.financeTracker.financeTracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.financeTracker.financeTracker.Enums.AggregationMode.MONTH;
import static com.financeTracker.financeTracker.Enums.AggregationMode.WEEK;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactions(Long fromEpoch, Long toEpoch) {

        Instant to;
        Instant from;

        if (fromEpoch != null && toEpoch != null) {

            if (fromEpoch > toEpoch) {
                throw new IllegalArgumentException("from cannot be greater than to");
            }

            from = Instant.ofEpochMilli(fromEpoch);
            to = Instant.ofEpochMilli(toEpoch);

        } else {
            to = Instant.now();                // UTC now
            from = to.minus(365, ChronoUnit.DAYS);
        }

        return transactionRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toDTO)
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

    @Override
    public List<AggregationResponseDTO> aggregate(
            AggregationMode mode,
            Long fromEpoch,
            Long toEpoch) {

        if (fromEpoch == null || toEpoch == null) {
            throw new IllegalArgumentException("from and to timestamps are required");
        }

        if (fromEpoch > toEpoch) {
            throw new IllegalArgumentException("from timeStamp cannot be greater than to");
        }

        Instant from = Instant.ofEpochMilli(fromEpoch);
        Instant to = Instant.ofEpochMilli(toEpoch);

        List<Object[]> rows;

        switch (mode) {
            case MONTH -> rows = transactionRepository.aggregateByMonth(from, to);
            case WEEK -> rows = transactionRepository.aggregateByWeek(from, to);
            default -> throw new IllegalArgumentException("Unsupported aggregation mode");
        }

        return rows.stream()
                .map(row -> AggregationResponseDTO.builder()
                        .periodStart(((java.sql.Timestamp) row[0]).toInstant())
                        .totalCredit((java.math.BigDecimal) row[1])
                        .totalDebit((java.math.BigDecimal) row[2])
                        .build())
                .toList();
    }


    // --------- Mapper methods ---------

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
        return Transaction.builder()
                .amount(dto.getAmount())
                .transactionType(dto.getTransactionType())
                .description(dto.getDescription())
                .build();
    }
}