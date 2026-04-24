package com.ims.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ims.model.User;

@Repository
public class UserDAO {

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users(name, department) VALUES (?, ?)";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getDepartment());
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY name ASC";
        List<User> users = new ArrayList<>();
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return users;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (var connection = DBConnection.getConnection();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private User mapUser(ResultSet resultSet) throws Exception {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setDepartment(resultSet.getString("department"));
        return user;
    }
}
