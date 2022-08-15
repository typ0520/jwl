package com.example.jwl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tong
 */
public class DBUtils {
    public static <T> void save(Connection conn, T model) throws Throwable {
        StringBuilder sqlBuilder = new StringBuilder("insert into ");
        StringBuilder placeholderBuilder = new StringBuilder();
        String sep = "";
        sqlBuilder.append(getTableName(model));
        sqlBuilder.append("(");
        for (Field field : model.getClass().getDeclaredFields()) {
            if (getPrimaryField(model) == field) {
                break;
            }
            sqlBuilder.append(sep).append(getFieldName(field));
            placeholderBuilder.append(sep).append("?");
            sep = ",";
        }
        sqlBuilder.append(") values(");
        sqlBuilder.append(placeholderBuilder);
        sqlBuilder.append(")");

        String sql = sqlBuilder.toString();
        System.out.println(sql);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             PreparedStatement preparedStatement2 = conn.prepareStatement("select LAST_INSERT_ID();");) {
            int idx = 1;
            for (Field field : model.getClass().getDeclaredFields()) {
                if (getPrimaryField(model) == field) {
                    break;
                }
                field.setAccessible(true);
                Object val = field.get(model);
                if (field.getType() == String.class) {
                    preparedStatement.setString(idx++, (String) val);
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    preparedStatement.setInt(idx++, (Integer) val);
                }
            }
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            ResultSet rs2 = preparedStatement2.executeQuery();
            if (rs2.next()) {
                Field primaryField = getPrimaryField(model);
                primaryField.setAccessible(true);
                primaryField.set(model, rs2.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static <T> Field getPrimaryField(T model) throws NoSuchFieldException {
        return model.getClass().getDeclaredField("id");
    }

    private static <T> String getFieldName(Field field) {
        return field.getName().toLowerCase();
    }

    private static <T> String getTableName(T model) {
        return model.getClass().getSimpleName().toLowerCase();
    }
}
