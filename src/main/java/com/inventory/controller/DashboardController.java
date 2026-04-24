package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.repository.AssetRepository;
import com.inventory.repository.TransactionRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Get user from session - use fully qualified import
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("totalAssets", assetRepository.count());
        model.addAttribute("availableAssets", assetRepository.findByStatus("available").size());
        model.addAttribute("activeTransactions", transactionRepository.findByStatus("issued").size());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("user", user);
        
        return "dashboard";
    }
}