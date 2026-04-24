package com.ims.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ims.model.Asset;

@Repository
public class AssetDAO {

    public boolean insertAsset(Asset asset) {
        String sql = "INSERT INTO assets(name, type, serial_number, condition_status, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, asset.getName());
            statement.setString(2, asset.getType());
            statement.setString(3, asset.getSerialNumber());
            statement.setString(4, asset.getConditionStatus());
            statement.setString(5, asset.getStatus());
            statement.setTimestamp(6, Timestamp.valueOf(asset.getCreatedAt()));
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean updateAsset(Asset asset) {
        String sql = "UPDATE assets SET name = ?, type = ?, serial_number = ?, condition_status = ?, status = ? "
                + "WHERE id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, asset.getName());
            statement.setString(2, asset.getType());
            statement.setString(3, asset.getSerialNumber());
            statement.setString(4, asset.getConditionStatus());
            statement.setString(5, asset.getStatus());
            statement.setInt(6, asset.getId());
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean deleteAsset(int id) {
        String sql = "DELETE FROM assets WHERE id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<Asset> getAllAssets() {
        String sql = "SELECT * FROM assets ORDER BY created_at DESC, id DESC";
        List<Asset> assets = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                assets.add(mapAsset(resultSet));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return assets;
    }

    public List<Asset> getAvailableAssets() {
        String sql = "SELECT * FROM assets WHERE LOWER(status) = 'available' ORDER BY name ASC";
        List<Asset> assets = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                assets.add(mapAsset(resultSet));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return assets;
    }

    public Asset getAssetById(int id) {
        String sql = "SELECT * FROM assets WHERE id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAsset(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public List<String> getAssetTypes() {
        String sql = "SELECT DISTINCT type FROM assets ORDER BY type ASC";
        List<String> types = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                types.add(resultSet.getString("type"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return types;
    }

    private Asset mapAsset(ResultSet resultSet) throws Exception {
        Asset asset = new Asset();
        asset.setId(resultSet.getInt("id"));
        asset.setName(resultSet.getString("name"));
        asset.setType(resultSet.getString("type"));
        asset.setSerialNumber(resultSet.getString("serial_number"));
        asset.setConditionStatus(resultSet.getString("condition_status"));
        asset.setStatus(resultSet.getString("status"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        asset.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return asset;
    }
}
