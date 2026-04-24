package com.inventory.service;

import com.inventory.model.Asset;
import com.inventory.model.Transaction;
import com.inventory.model.User;
import com.inventory.repository.AssetRepository;
import com.inventory.repository.TransactionRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Transaction assignAsset(Long assetId, Long userId, String message) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        
        if (!"available".equals(asset.getStatus())) {
            throw new RuntimeException("Asset is not available");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Transaction transaction = new Transaction();
        transaction.setAsset(asset);
        transaction.setUser(user);
        transaction.setIssueDate(LocalDateTime.now());
        transaction.setStatus("issued");
        transaction.setMessage(message);
        
        asset.setStatus("assigned");
        assetRepository.save(asset);
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction returnAsset(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        transaction.setReturnDate(LocalDateTime.now());
        transaction.setStatus("returned");
        
        Asset asset = transaction.getAsset();
        asset.setStatus("available");
        assetRepository.save(asset);
        
        return transactionRepository.save(transaction);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<Transaction> getActiveTransactions() {
        return transactionRepository.findActiveTransactions();
    }
}