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
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void save(Student student) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into student(name, email) values (?,?)");
             PreparedStatement preparedStatement2 = connection.prepareStatement("select LAST_INSERT_ID();");) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.executeUpdate();

            ResultSet rs2 = preparedStatement2.executeQuery();
            if (rs2.next()) {
                student.setId(rs2.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(Student student) {
        boolean updated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("update student set name=?,email=? where id=?");) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setInt(3, student.getId());
            System.out.println(statement);
            updated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    public Student findById(int id) {
        Student student = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select name, email from student where id = ?;");) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                student = new Student(id, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public boolean delete(Student student) {
        boolean deleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from student where id = ?");) {
            statement.setInt(1, student.getId());
            deleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted;
    }
}