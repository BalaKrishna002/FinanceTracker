package com.financeTracker.financeTracker.repository;

import com.financeTracker.financeTracker.model.Transaction;
import com.financeTracker.financeTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserAndCreatedAtBetween(
            User user,
            Instant from,
            Instant to
    );

    // -------- Monthly aggregation --------
    @Query(value = """
        SELECT
          date_trunc('month', created_at) AS periodStart,
          SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END) AS totalCredit,
          SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END) AS totalDebit
        FROM transactions
        WHERE user_id = :userId AND created_at BETWEEN :from AND :to
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<Object[]> aggregateByMonth(Long id, Instant from, Instant to);

    // -------- Weekly aggregation --------
    @Query(value = """
        SELECT
          date_trunc('week', created_at) AS periodStart,
          SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END) AS totalCredit,
          SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END) AS totalDebit
        FROM transactions
        WHERE user_id = :userId AND created_at BETWEEN :from AND :to
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<Object[]> aggregateByWeek(Long id, Instant from, Instant to);

    // -------- Daily aggregation --------
    @Query(value = """
        SELECT
          date_trunc('day', created_at) AS periodStart,
          SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END) AS totalCredit,
          SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END) AS totalDebit
        FROM transactions
        WHERE user_id = :userId AND created_at BETWEEN :from AND :to
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<Object[]> aggregateByDay(Long id, Instant from, Instant to);
}
