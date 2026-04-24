package com.ims.model;

import java.time.LocalDateTime;

public class Transaction {

    private Integer id;
    private Integer assetId;
    private Integer userId;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    private String status;
    private String message;
    private String assetName;
    private String assetType;
    private String userName;
    private String department;

    public Transaction() {
    }

    public Transaction(Integer id, Integer assetId, Integer userId, LocalDateTime issueDate, LocalDateTime returnDate,
            String status, String message, String assetName, String assetType, String userName, String department) {
        this.id = id;
        this.assetId = assetId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.message = message;
        this.assetName = assetName;
        this.assetType = assetType;
        this.userName = userName;
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
