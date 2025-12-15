package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.TransactionDTO;
import com.financeTracker.financeTracker.model.Transaction;
import com.financeTracker.financeTracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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