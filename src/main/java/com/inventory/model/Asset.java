package com.inventory.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Data
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "serial_number", unique = true)
    private String serialNumber;
    
    @Column(name = "condition_status")
    private String conditionStatus;
    
    @Column(nullable = false)
    private String status = "available"; // available, assigned, maintenance, retired
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}