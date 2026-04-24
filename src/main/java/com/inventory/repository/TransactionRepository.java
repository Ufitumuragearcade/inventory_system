package com.inventory.repository;

import com.inventory.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByStatus(String status);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'issued'")
    List<Transaction> findActiveTransactions();
    
    @Query("SELECT t FROM Transaction t WHERE t.issueDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.department = :department")
    List<Transaction> findByDepartment(String department);
}