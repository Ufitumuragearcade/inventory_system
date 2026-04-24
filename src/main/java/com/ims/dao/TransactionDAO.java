package com.ims.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ims.model.Transaction;

@Repository
public class TransactionDAO {

    private static final String JOIN_QUERY = "SELECT t.*, a.name AS asset_name, a.type AS asset_type, "
            + "u.name AS user_name, u.department AS department "
            + "FROM transactions t "
            + "JOIN assets a ON t.asset_id = a.id "
            + "JOIN users u ON t.user_id = u.id ";

    public boolean assignAsset(Transaction transaction) {
        String sql = "INSERT INTO transactions(asset_id, user_id, issue_date, return_date, status, message) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transaction.getAssetId());
            statement.setInt(2, transaction.getUserId());
            statement.setTimestamp(3, Timestamp.valueOf(transaction.getIssueDate()));
            statement.setTimestamp(4, transaction.getReturnDate() == null ? null : Timestamp.valueOf(transaction.getReturnDate()));
            statement.setString(5, transaction.getStatus());
            statement.setString(6, transaction.getMessage());
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean returnAsset(int transactionId, LocalDateTime returnDate, String message) {
        String sql = "UPDATE transactions SET return_date = ?, status = ?, message = ? WHERE id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(returnDate));
            statement.setString(2, "returned");
            statement.setString(3, message);
            statement.setInt(4, transactionId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getAllTransactions() {
        String sql = JOIN_QUERY + "ORDER BY t.issue_date DESC, t.id DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapTransaction(resultSet));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getFilteredTransactions(LocalDate startDate, LocalDate endDate, String assetType,
            String department, String status) {
        StringBuilder sql = new StringBuilder(JOIN_QUERY).append("WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();

        if (startDate != null) {
            sql.append("AND DATE(t.issue_date) >= ? ");
            parameters.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND DATE(t.issue_date) <= ? ");
            parameters.add(Date.valueOf(endDate));
        }
        if (assetType != null && !assetType.isBlank()) {
            sql.append("AND a.type = ? ");
            parameters.add(assetType);
        }
        if (department != null && !department.isBlank()) {
            sql.append("AND u.department = ? ");
            parameters.add(department);
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND t.status = ? ");
            parameters.add(status);
        }

        sql.append("ORDER BY t.issue_date DESC, t.id DESC");

        List<Transaction> transactions = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(resultSet));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getActiveTransactions() {
        String sql = JOIN_QUERY + "WHERE LOWER(t.status) = 'assigned' ORDER BY t.issue_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapTransaction(resultSet));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return transactions;
    }

    public Transaction getTransactionById(int id) {
        String sql = JOIN_QUERY + "WHERE t.id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTransaction(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean hasActiveTransactionForAsset(int assetId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE asset_id = ? AND LOWER(status) = 'assigned'";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean hasAnyTransactionForAsset(int assetId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE asset_id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private Transaction mapTransaction(ResultSet resultSet) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setAssetId(resultSet.getInt("asset_id"));
        transaction.setUserId(resultSet.getInt("user_id"));
        Timestamp issueDate = resultSet.getTimestamp("issue_date");
        Timestamp returnDate = resultSet.getTimestamp("return_date");
        transaction.setIssueDate(issueDate == null ? null : issueDate.toLocalDateTime());
        transaction.setReturnDate(returnDate == null ? null : returnDate.toLocalDateTime());
        transaction.setStatus(resultSet.getString("status"));
        transaction.setMessage(resultSet.getString("message"));
        transaction.setAssetName(resultSet.getString("asset_name"));
        transaction.setAssetType(resultSet.getString("asset_type"));
        transaction.setUserName(resultSet.getString("user_name"));
        transaction.setDepartment(resultSet.getString("department"));
        return transaction;
    }
}
