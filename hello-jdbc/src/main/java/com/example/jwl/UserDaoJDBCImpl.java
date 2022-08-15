package com.example.jwl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CREATE TABLE `user` (
 *   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
 *   `name` int(11) DEFAULT NULL,
 *   `email` int(11) DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */
public class UserDaoJDBCImpl {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
    private String jdbcUsername = "root";
    private String jdbcPassword = "root";

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void save(User user) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into user(name, email) values (?,?)");
             PreparedStatement preparedStatement2 = connection.prepareStatement("select LAST_INSERT_ID();");) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.executeUpdate();

            ResultSet rs2 = preparedStatement2.executeQuery();
            if (rs2.next()) {
                user.setId(rs2.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(User user) {
        boolean updated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("update user set name=?,email=? where id=?");) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getId());
            System.out.println(statement);
            updated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    public User findById(int id) {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select name, email from user where id = ?;");) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                user = new User(id, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean delete(User user) {
        boolean deleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from user where id = ?");) {
            statement.setInt(1, user.getId());
            deleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted;
    }
}