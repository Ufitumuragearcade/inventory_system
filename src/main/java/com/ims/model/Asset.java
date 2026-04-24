package com.ims.model;

import java.time.LocalDateTime;

public class Asset {

    private Integer id;
    private String name;
    private String type;
    private String serialNumber;
    private String conditionStatus;
    private String status;
    private LocalDateTime createdAt;

    public Asset() {
    }

    public Asset(Integer id, String name, String type, String serialNumber, String conditionStatus, String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.conditionStatus = conditionStatus;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
