package com.example.jwl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tong
 */
public class DBUtils {
    public static <T> void save(Connection conn, T model) throws Throwable {
        StringBuilder sqlBuilder = new StringBuilder("insert into ");
        StringBuilder placeholderBuilder = new StringBuilder();
        String sep = "";
        sqlBuilder.append(getTableName(model.getClass()));
        sqlBuilder.append("(");
        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.equals(getPrimaryField(model.getClass()))) {
                continue;
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
                if (field.equals(getPrimaryField(model.getClass()))) {
                    continue;
                }
                field.setAccessible(true);
                Object val = field.get(model);
                if (field.getType() == String.class) {
                    preparedStatement.setString(idx++, (String) val);
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    preparedStatement.setInt(idx++, (Integer) val);
                } else {
                    //TODO 暂不实现
                }
            }
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            ResultSet rs2 = preparedStatement2.executeQuery();
            if (rs2.next()) {
                Field primaryField = getPrimaryField(model.getClass());
                primaryField.setAccessible(true);
                primaryField.set(model, rs2.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean update(Connection conn, Object model) throws Throwable {
        //update student set name=?,email=? where id=?
        boolean updated = false;
        StringBuilder sqlBuilder = new StringBuilder("update ");
        String sep = "";
        sqlBuilder.append(getTableName(model.getClass()));
        sqlBuilder.append(" set ");
        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.equals(getPrimaryField(model.getClass()))) {
                continue;
            }
            sqlBuilder.append(sep).append(getFieldName(field)).append("=?");
            sep = ",";
        }
        sqlBuilder.append(" where id = ?");

        String sql = sqlBuilder.toString();
        System.out.println(sql);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            int idx = 1;
            for (Field field : model.getClass().getDeclaredFields()) {
                if (field.equals(getPrimaryField(model.getClass()))) {
                    continue;
                }
                field.setAccessible(true);
                Object val = field.get(model);
                if (field.getType() == String.class) {
                    preparedStatement.setString(idx++, (String) val);
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    preparedStatement.setInt(idx++, (Integer) val);
                } else {
                    //TODO 暂不实现
                }
            }
            Field primaryField = getPrimaryField(model.getClass());
            primaryField.setAccessible(true);
            preparedStatement.setInt(idx, (Integer) primaryField.get(model));
            System.out.println(preparedStatement);
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    public static boolean delete(Connection conn, Object model) throws Throwable {
        //delete from student where id = ?
        boolean deleted = false;
        String sql = "delete from " + getTableName(model.getClass()) + " where id = ?";
        Field primaryField = getPrimaryField(model.getClass());
        primaryField.setAccessible(true);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            preparedStatement.setInt(1, (Integer) primaryField.get(model));
            System.out.println(preparedStatement);
            deleted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public static <T> T findById(Connection conn, java.lang.Class<T> clazz, Object id) throws Throwable {
        return findById(conn, clazz, id, new HashMap());
    }

    private static <T> T findById(Connection conn, java.lang.Class<T> clazz, Object id, Map memos) throws Throwable {
        //select id, name, email from student where id = ?
        T result = null;
        StringBuilder sqlBuilder = new StringBuilder("select ");
        String sep = "";
        for (Field field : clazz.getDeclaredFields()) {
            sqlBuilder.append(sep);
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne != null) {
                sqlBuilder.append(oneToOne.joinColumn());
            } else {
                sqlBuilder.append(getFieldName(field));
            }
            sep = ",";
        }
        sqlBuilder.append(" from ").append(getTableName(clazz));
        sqlBuilder.append(" where id = ?");
        String sql = sqlBuilder.toString();
        System.out.println(sql);

        Field primaryField = getPrimaryField(clazz);
        primaryField.setAccessible(true);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            if (id.getClass() == String.class) {
                preparedStatement.setString(1, (String) id);
            } else if (id.getClass() == Integer.class) {
                preparedStatement.setInt(1, (Integer) id);
            } else {
                //TODO 暂不实现
            }
            System.out.println(preparedStatement);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                result = clazz.newInstance();
                memos.put(id, result);
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getType() == String.class) {
                        field.set(result, rs.getString(getFieldName(field)));
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(result, rs.getInt(getFieldName(field)));
                    } else {
                        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                        if (oneToOne != null) {
                            Object val = rs.getObject(oneToOne.joinColumn());
                            field.set(result, memos.getOrDefault(id, findById(conn, field.getType(), val)));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Field getPrimaryField(java.lang.Class clazz) throws NoSuchFieldException {
        Field targetField = null;
        for (Field field : clazz.getDeclaredFields()) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                return field;
            }
        }
        return clazz.getDeclaredField("id");
    }

    private static String getFieldName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.name();
        }
        return field.getName().toLowerCase();
    }

    private static String getTableName(java.lang.Class clazz) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }
}
