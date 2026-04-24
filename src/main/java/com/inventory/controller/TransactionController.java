package com.inventory.controller;

import com.inventory.service.InventoryService;
import com.inventory.service.TransactionService;
import com.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;

@Controller
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/assign")
    public String assignForm(Model model, HttpSession session) {
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("availableAssets", inventoryService.getAvailableAssets());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", user);
        return "assign";
    }
    
    @PostMapping("/assign")
    public String assignAsset(@RequestParam Long assetId,
                             @RequestParam Long userId,
                             @RequestParam(required = false) String message,
                             RedirectAttributes redirectAttributes) {
        try {
            transactionService.assignAsset(assetId, userId, message);
            redirectAttributes.addFlashAttribute("success", "Asset assigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/report";
    }
    
    @GetMapping("/return")
    public String returnForm(Model model, HttpSession session) {
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("activeTransactions", transactionService.getActiveTransactions());
        model.addAttribute("user", user);
        return "return";
    }
    
    @PostMapping("/return")
    public String returnAsset(@RequestParam Long transactionId,
                             RedirectAttributes redirectAttributes) {
        try {
            transactionService.returnAsset(transactionId);
            redirectAttributes.addFlashAttribute("success", "Asset returned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/report";
    }
    
    @GetMapping("/report")
    public String reportPage(Model model, HttpSession session) {
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("transactions", transactionService.getAllTransactions());
        model.addAttribute("user", user);
        return "report";
    }
}