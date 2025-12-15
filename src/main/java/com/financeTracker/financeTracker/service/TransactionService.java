package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.AggregationResponseDTO;
import com.financeTracker.financeTracker.DTO.TransactionDTO;
import com.financeTracker.financeTracker.Enums.AggregationMode;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> getAllTransactions();

    List<TransactionDTO> getTransactions(Long fromEpoch, Long toEpoch);

    TransactionDTO getTransactionById(Long id);

    TransactionDTO createTransaction(TransactionDTO dto);

    List<TransactionDTO> createTransactions(List<TransactionDTO> transactions);

    void deleteTransactionById(Long id);

    List<AggregationResponseDTO> aggregate(
            AggregationMode mode,
            Long fromEpoch,
            Long toEpoch
    );

}
