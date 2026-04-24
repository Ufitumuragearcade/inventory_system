package com.ims.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ims.service.InventoryService;
import com.ims.util.Helpers;

import jakarta.servlet.http.HttpSession;

@Controller
public class AssignForm {

    private final InventoryService inventoryService;

    public AssignForm(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/assign")
    public String showAssignPage(Model model, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "assign");
        model.addAttribute("users", inventoryService.getAllUsers());
        model.addAttribute("availableAssets", inventoryService.getAvailableAssets());
        model.addAttribute("recentTransactions", inventoryService.getAllTransactions().stream().limit(5).toList());
        return "assign";
    }

    @PostMapping("/assign")
    public String assignAsset(@RequestParam int assetId, @RequestParam int userId,
            @RequestParam(required = false) String message, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            inventoryService.assignAsset(assetId, userId, message);
            redirectAttributes.addFlashAttribute("successMessage", "Asset assigned successfully.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/assign";
    }
}
