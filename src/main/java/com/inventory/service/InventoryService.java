package com.inventory.service;

import com.inventory.model.Asset;
import com.inventory.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class InventoryService {
    
    @Autowired
    private AssetRepository assetRepository;
    
    public Asset addAsset(Asset asset) {
        if (asset.getStatus() == null) {
            asset.setStatus("available");
        }
        return assetRepository.save(asset);
    }
    
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    
    public List<Asset> getAvailableAssets() {
        return assetRepository.findAvailableAssets();
    }
    
    public Asset getAssetById(Long id) {
        return assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
    }
    
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
}