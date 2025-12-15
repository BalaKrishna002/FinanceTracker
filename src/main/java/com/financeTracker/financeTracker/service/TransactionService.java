package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.TransactionDTO;
import java.util.List;

public interface TransactionService {

    List<TransactionDTO> getAllTransactions();

    TransactionDTO getTransactionById(Long id);

    TransactionDTO createTransaction(TransactionDTO dto);

    List<TransactionDTO> createTransactions(List<TransactionDTO> transactions);

    void deleteTransactionById(Long id);
}
