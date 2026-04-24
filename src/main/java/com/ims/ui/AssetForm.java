package com.ims.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ims.model.Asset;
import com.ims.service.InventoryService;
import com.ims.util.Helpers;

import jakarta.servlet.http.HttpSession;

@Controller
public class AssetForm {

    private final InventoryService inventoryService;

    public AssetForm(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/assets")
    public String showAssets(@RequestParam(required = false) Integer editId, Model model, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        Asset assetForm = editId == null ? new Asset() : inventoryService.getAssetById(editId);
        if (assetForm == null) {
            assetForm = new Asset();
        }

        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("activePage", "assets");
        model.addAttribute("assetForm", assetForm);
        model.addAttribute("assets", inventoryService.getAllAssets());
        model.addAttribute("conditionOptions", List.of("Excellent", "Good", "Fair", "Damaged"));
        model.addAttribute("statusOptions", List.of("available", "assigned", "returned", "maintenance"));
        return "asset-form";
    }

    @PostMapping("/assets/save")
    public String saveAsset(@ModelAttribute("assetForm") Asset asset, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            if (asset.getId() == null) {
                inventoryService.addAsset(asset);
                redirectAttributes.addFlashAttribute("successMessage", "Asset created successfully.");
            } else {
                inventoryService.updateAsset(asset);
                redirectAttributes.addFlashAttribute("successMessage", "Asset updated successfully.");
            }
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/assets";
    }

    @GetMapping("/assets/edit/{id}")
    public String editAsset(@PathVariable int id, HttpSession session) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }
        return "redirect:/assets?editId=" + id;
    }

    @GetMapping("/assets/delete/{id}")
    public String deleteAsset(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Helpers.isAuthenticated(session)) {
            return "redirect:/login";
        }

        try {
            inventoryService.deleteAsset(id);
            redirectAttributes.addFlashAttribute("successMessage", "Asset deleted successfully.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/assets";
    }
}
