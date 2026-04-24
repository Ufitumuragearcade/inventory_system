package com.ims.ui;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ims.service.InventoryService;
import com.ims.util.Helpers;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReportForm {

    private final InventoryService inventoryService;

    public ReportForm(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/reports")
    public String showReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "reports");
        model.addAttribute("transactions",
                inventoryService.getFilteredTransactions(startDate, endDate, assetType, department, status));
        model.addAttribute("assetTypes", inventoryService.getAssetTypes());
        model.addAttribute("departments", inventoryService.getAllUsers().stream()
                .map(user -> user.getDepartment())
                .filter(Helpers::hasText)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("assetType", assetType);
        model.addAttribute("department", department);
        model.addAttribute("status", status);
        return "report";
    }
}
