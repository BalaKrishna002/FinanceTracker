package com.financeTracker.financeTracker.controller;

import com.financeTracker.financeTracker.DTO.AggregationResponseDTO;
import com.financeTracker.financeTracker.DTO.TransactionDTO;
import com.financeTracker.financeTracker.Enums.AggregationMode;
import com.financeTracker.financeTracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // Get all transactions
//    @GetMapping
//    public List<TransactionDTO> getAllTransactions() {
//        return transactionService.getAllTransactions();
//    }

    @GetMapping
    public List<TransactionDTO> getTransactions(
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to) {
        return transactionService.getTransactions(from, to);
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public TransactionDTO getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    // Create new transaction
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@RequestBody TransactionDTO dto) {
        return transactionService.createTransaction(dto);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TransactionDTO> createTransactions(
            @RequestBody List<TransactionDTO> transactions) {
        return transactionService.createTransactions(transactions);
    }

    // Delete transaction by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
    }

    @GetMapping("/aggregation")
    public List<AggregationResponseDTO> aggregateTransactions(
            @RequestParam AggregationMode mode,
            @RequestParam Long from,
            @RequestParam Long to) {
        return transactionService.aggregate(mode, from, to);
    }

}
