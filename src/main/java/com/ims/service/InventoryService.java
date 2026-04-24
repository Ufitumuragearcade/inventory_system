package com.ims.service;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.ims.dao.AssetDAO;
import com.ims.dao.DBConnection;
import com.ims.dao.TransactionDAO;
import com.ims.dao.UserDAO;
import com.ims.model.Asset;
import com.ims.model.Transaction;
import com.ims.model.User;
import com.ims.util.Helpers;

@Service
public class InventoryService {

    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());

    private final AssetDAO assetDAO;
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    public InventoryService(AssetDAO assetDAO, UserDAO userDAO, TransactionDAO transactionDAO) {
        this.assetDAO = assetDAO;
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    public boolean addAsset(Asset asset) {
        asset.setName(Helpers.safe(asset.getName()));
        asset.setType(Helpers.safe(asset.getType()));
        asset.setSerialNumber(Helpers.safe(asset.getSerialNumber()));
        asset.setConditionStatus(Helpers.nvl(asset.getConditionStatus(), "Good"));
        asset.setStatus(Helpers.nvl(asset.getStatus(), "available").toLowerCase(Locale.ENGLISH));
        asset.setCreatedAt(LocalDateTime.now());

        if (!Helpers.hasText(asset.getName()) || !Helpers.hasText(asset.getType())
                || !Helpers.hasText(asset.getSerialNumber())) {
            throw new IllegalArgumentException("Name, type, and serial number are required.");
        }

        boolean created = assetDAO.insertAsset(asset);
        if (!created) {
            throw new IllegalStateException("Asset could not be saved. Check if the serial number already exists.");
        }
        logAudit("ASSET_CREATED", "Asset added: " + asset.getName() + " (" + asset.getSerialNumber() + ")");
        return created;
    }

    public boolean updateAsset(Asset asset) {
        if (asset.getId() == null) {
            throw new IllegalArgumentException("Asset id is required for update.");
        }

        asset.setName(Helpers.safe(asset.getName()));
        asset.setType(Helpers.safe(asset.getType()));
        asset.setSerialNumber(Helpers.safe(asset.getSerialNumber()));
        asset.setConditionStatus(Helpers.nvl(asset.getConditionStatus(), "Good"));
        asset.setStatus(Helpers.nvl(asset.getStatus(), "available").toLowerCase(Locale.ENGLISH));

        boolean updated = assetDAO.updateAsset(asset);
        if (!updated) {
            throw new IllegalStateException("Asset could not be updated.");
        }
        logAudit("ASSET_UPDATED", "Asset updated: #" + asset.getId() + " - " + asset.getName());
        return updated;
    }

    public boolean deleteAsset(int assetId) {
        if (transactionDAO.hasActiveTransactionForAsset(assetId)) {
            throw new IllegalStateException("You cannot delete an asset that is currently assigned.");
        }
        if (transactionDAO.hasAnyTransactionForAsset(assetId)) {
            throw new IllegalStateException("You cannot delete an asset that already has transaction history.");
        }

        Asset asset = assetDAO.getAssetById(assetId);
        boolean deleted = assetDAO.deleteAsset(assetId);
        if (!deleted) {
            throw new IllegalStateException("Asset could not be deleted.");
        }
        if (asset != null) {
            logAudit("ASSET_DELETED", "Asset deleted: " + asset.getName() + " (" + asset.getSerialNumber() + ")");
        }
        return deleted;
    }

    public List<Asset> getAllAssets() {
        return assetDAO.getAllAssets();
    }

    public List<Asset> getAvailableAssets() {
        return assetDAO.getAvailableAssets();
    }

    public Asset getAssetById(int id) {
        return assetDAO.getAssetById(id);
    }

    public List<String> getAssetTypes() {
        return assetDAO.getAssetTypes();
    }

    public boolean addUser(User user) {
        user.setName(Helpers.safe(user.getName()));
        user.setDepartment(Helpers.safe(user.getDepartment()));

        if (!Helpers.hasText(user.getName()) || !Helpers.hasText(user.getDepartment())) {
            throw new IllegalArgumentException("User name and department are required.");
        }

        boolean created = userDAO.insertUser(user);
        if (!created) {
            throw new IllegalStateException("User could not be saved.");
        }
        logAudit("USER_CREATED", "User added: " + user.getName() + " (" + user.getDepartment() + ")");
        return created;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean assignAsset(int assetId, int userId, String note) {
        Asset asset = assetDAO.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalStateException("Selected asset was not found.");
        }
        if (!"available".equalsIgnoreCase(asset.getStatus())) {
            throw new IllegalStateException("Asset cannot be assigned because it is not available.");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalStateException("Selected user was not found.");
        }

        Transaction transaction = new Transaction();
        transaction.setAssetId(assetId);
        transaction.setUserId(userId);
        transaction.setIssueDate(LocalDateTime.now());
        transaction.setStatus("assigned");
        transaction.setMessage("Issue Note: " + Helpers.nvl(note, "No issue note provided."));

        boolean assigned = transactionDAO.assignAsset(transaction);
        if (!assigned) {
            throw new IllegalStateException("Assignment could not be saved.");
        }

        asset.setStatus("assigned");
        boolean updated = assetDAO.updateAsset(asset);
        if (!updated) {
            throw new IllegalStateException("Asset was assigned, but the asset status could not be updated.");
        }
        logAudit("ASSET_ASSIGNED",
                "Asset " + asset.getName() + " assigned to " + user.getName() + " in " + user.getDepartment());
        return updated;
    }

    public boolean returnAsset(int transactionId, String returnMessage) {
        Transaction transaction = transactionDAO.getTransactionById(transactionId);
        if (transaction == null) {
            throw new IllegalStateException("Selected transaction was not found.");
        }
        if (!"assigned".equalsIgnoreCase(transaction.getStatus())) {
            throw new IllegalStateException("Only assigned assets can be returned.");
        }

        Asset asset = assetDAO.getAssetById(transaction.getAssetId());
        if (asset == null) {
            throw new IllegalStateException("Linked asset was not found.");
        }

        String mergedMessage = Helpers.nvl(transaction.getMessage(), "Issue Note: None")
                + System.lineSeparator()
                + "Return Note: " + Helpers.nvl(returnMessage, "No return note provided.");

        boolean returned = transactionDAO.returnAsset(transactionId, LocalDateTime.now(), mergedMessage);
        if (!returned) {
            throw new IllegalStateException("Return transaction could not be saved.");
        }

        asset.setStatus("returned");
        boolean updated = assetDAO.updateAsset(asset);
        if (!updated) {
            throw new IllegalStateException("Asset was returned, but the asset status could not be updated.");
        }
        logAudit("ASSET_RETURNED",
                "Asset " + asset.getName() + " returned from " + transaction.getUserName());
        return updated;
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getFilteredTransactions(LocalDate startDate, LocalDate endDate, String assetType,
            String department, String status) {
        return transactionDAO.getFilteredTransactions(startDate, endDate, assetType, department, status);
    }

    public List<Transaction> getActiveTransactions() {
        return transactionDAO.getActiveTransactions();
    }

    private void logAudit(String actionType, String details) {
        String sql = "INSERT INTO audit_logs(action_type, details) VALUES (?, ?)";
        try (var connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, actionType);
            statement.setString(2, details);
            statement.executeUpdate();
        } catch (Exception exception) {
            LOGGER.log(Level.WARNING, "Could not store audit log: " + details, exception);
        }
    }
}
