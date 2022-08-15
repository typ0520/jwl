package com.example.jwl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author tong
 */
public class StudentDaoMyORMImplTest {
    private StudentDaoMyORMImpl studentDao = new StudentDaoMyORMImpl();

    @Test
    public void test_getConnection() {
        studentDao.getConnection();
    }


    @Test
    public void test_save() {
        Student student = new Student(0, "test", "test@qq.com");
        studentDao.save(student);
        Assert.assertNotEquals(student.getId(), 0);
    }

    @Test
    public void test_update() {
        Student student = new Student(15, "test2", "test2@qq.com");
        studentDao.update(student);
    }

    @Test
    public void test_findById() {
        Student student = studentDao.findById(15);
        Assert.assertNotNull(student);
        System.out.println(student);
    }

    @Test
    public void test_delete() {
        Student student = new Student(0, "testdelete", "testdelete@qq.com");
        studentDao.save(student);
        System.out.println(student);
        studentDao.delete(student);
        Student student2 = studentDao.findById(student.getId());
        Assert.assertNull(student2);
    }
}
