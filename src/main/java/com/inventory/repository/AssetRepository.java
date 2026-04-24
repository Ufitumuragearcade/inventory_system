package com.inventory.repository;

import com.inventory.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    List<Asset> findByStatus(String status);
    
    List<Asset> findByType(String type);
    
    Asset findBySerialNumber(String serialNumber);
    
    @Query("SELECT a FROM Asset a WHERE a.status = 'available'")
    List<Asset> findAvailableAssets();
}