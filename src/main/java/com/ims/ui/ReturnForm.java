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
public class ReturnForm {

    private final InventoryService inventoryService;

    public ReturnForm(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/return")
    public String showReturnPage(Model model, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "return");
        model.addAttribute("activeTransactions", inventoryService.getActiveTransactions());
        return "return";
    }

    @PostMapping("/return")
    public String returnAsset(@RequestParam int transactionId, @RequestParam(required = false) String message,
            HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            inventoryService.returnAsset(transactionId, message);
            redirectAttributes.addFlashAttribute("successMessage", "Asset returned successfully.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/return";
    }
}
