package com.inventory.controller;

import com.inventory.model.Asset;
import com.inventory.model.User;
import com.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/assets")
public class AssetController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping
    public String listAssets(Model model, HttpSession session) {
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("assets", inventoryService.getAllAssets());
        model.addAttribute("user", user);
        return "assets";
    }
    
    @GetMapping("/add")
    public String addAssetForm(Model model, HttpSession session) {
        com.inventory.model.User user = (com.inventory.model.User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("asset", new Asset());
        model.addAttribute("user", user);
        return "asset-form";
    }
    
    @PostMapping("/add")
    public String addAsset(@ModelAttribute Asset asset, RedirectAttributes redirectAttributes) {
        inventoryService.addAsset(asset);
        redirectAttributes.addFlashAttribute("success", "Asset added successfully");
        return "redirect:/assets";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteAsset(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        inventoryService.deleteAsset(id);
        redirectAttributes.addFlashAttribute("success", "Asset deleted");
        return "redirect:/assets";
    }
}