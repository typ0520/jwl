package com.example.jwl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDaoJDBCImpl {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
    private String jdbcUsername = "root";
    private String jdbcPassword = "root";

    protected Connection getConnection() {
        Connection connection = null;
        try {
            java.lang.Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void save(Student stu) {
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("insert into student(name, email) values (?,?)");
             PreparedStatement preparedStatement2 = conn.prepareStatement("select LAST_INSERT_ID();");) {
            preparedStatement.setString(1, stu.getName());
            preparedStatement.setString(2, stu.getEmail());
            preparedStatement.executeUpdate();

            ResultSet rs2 = preparedStatement2.executeQuery();
            if (rs2.next()) {
                stu.setId(rs2.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(Student stu) {
        boolean updated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("update student set name=?,email=? where id=?");) {
            statement.setString(1, stu.getName());
            statement.setString(2, stu.getEmail());
            statement.setInt(3, stu.getId());
            System.out.println(statement);
            updated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    public Student findById(int id) {
        Student stu = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select name, email from student where id = ?;");) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                stu = new Student(id, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stu;
    }

    public boolean delete(Student stu) {
        boolean deleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from student where id = ?");) {
            statement.setInt(1, stu.getId());
            deleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted;
    }
}