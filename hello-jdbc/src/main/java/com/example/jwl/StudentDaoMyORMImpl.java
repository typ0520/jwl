package com.example.jwl;

import java.sql.*;

public class StudentDaoMyORMImpl {
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
        try {
            DBUtils.save(getConnection(), stu);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Student stu) {
        try {
            return DBUtils.update(getConnection(), stu);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Student findById(int id) {
        try {
            return DBUtils.findById(getConnection(), Student.class, id);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(Student stu) {
        try {
            return DBUtils.delete(getConnection(), stu);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}