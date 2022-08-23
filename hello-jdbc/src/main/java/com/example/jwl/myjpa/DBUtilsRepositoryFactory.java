package com.example.jwl.myjpa;

import com.example.jwl.DBUtils;
import com.example.jwl.jpa.JpaRepository;
import com.example.jwl.jpa.JpaRepositoryFactory;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author tong
 */
public class DBUtilsRepositoryFactory implements JpaRepositoryFactory {

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

    public <T, ID> JpaRepository<T, ID> createRepository(Class<? extends JpaRepository<T, ID>> repoClass) {
        return (JpaRepository<T, ID>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ repoClass }, (proxy, method, args) -> {
            if ("save".equals(method.getName())) {
                Object entity = args[0];
                DBUtils.save(getConnection(), entity);
                return null;
            } else {
                //TODO 暂时不实现
            }
            return null;
        });
    }
}
