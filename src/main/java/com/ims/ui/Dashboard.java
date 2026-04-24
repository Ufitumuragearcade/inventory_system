package com.ims.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ims.model.Asset;
import com.ims.model.Transaction;
import com.ims.model.User;
import com.ims.service.InventoryService;
import com.ims.util.Helpers;

import jakarta.servlet.http.HttpSession;

@Controller
public class Dashboard {

    private final InventoryService inventoryService;

    public Dashboard(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        List<Asset> assets = inventoryService.getAllAssets();
        List<Transaction> transactions = inventoryService.getAllTransactions();
        List<User> users = inventoryService.getAllUsers();

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("totalAssets", assets.size());
        model.addAttribute("availableAssets", assets.stream().filter(asset -> "available".equalsIgnoreCase(asset.getStatus())).count());
        model.addAttribute("assignedAssets", assets.stream().filter(asset -> "assigned".equalsIgnoreCase(asset.getStatus())).count());
        model.addAttribute("returnedAssets", assets.stream().filter(asset -> "returned".equalsIgnoreCase(asset.getStatus())).count());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("recentTransactions", transactions.stream().limit(6).toList());
        return "dashboard";
    }

    @GetMapping("/users")
    public String showUsers(Model model, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "users");
        model.addAttribute("users", inventoryService.getAllUsers());
        model.addAttribute("userForm", new User());
        return "users";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("userForm") User user, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            inventoryService.addUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User saved successfully.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/users";
    }
}
