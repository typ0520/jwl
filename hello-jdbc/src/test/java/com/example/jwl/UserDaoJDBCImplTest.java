package com.example.jwl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author tong
 */
public class UserDaoJDBCImplTest {
    private UserDaoJDBCImpl userDAO = new UserDaoJDBCImpl();

    @Test
    public void test_getConnection() {
        userDAO.getConnection();
    }


    @Test
    public void test_save() {
        User user = new User(0, "test", "test@qq.com");
        userDAO.save(user);
        Assert.assertNotEquals(user.getId(), 0);
    }

    @Test
    public void test_update() {
        User user = new User(1, "test2", "test2@qq.com");
        userDAO.update(user);
    }

    @Test
    public void test_findById() {
        User user = userDAO.findById(1);
        Assert.assertNotNull(user);
        System.out.println(user);
    }

    @Test
    public void test_delete() {
        User user = new User(0, "testdelete", "testdelete@qq.com");
        userDAO.save(user);
        System.out.println(user);
        userDAO.delete(user);
        User user2 = userDAO.findById(user.getId());
        Assert.assertNull(user2);
    }
}
