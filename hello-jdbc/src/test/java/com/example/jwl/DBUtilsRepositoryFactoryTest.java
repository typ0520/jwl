package com.example.jwl;

import com.example.jwl.jpa.JpaRepository;
import com.example.jwl.jpa.JpaRepositoryFactory;
import com.example.jwl.myjpa.DBUtilsRepositoryFactory;
import com.example.jwl.myjpa.StudentRepository;
import org.junit.Test;

/**
 * @author tong
 */
public class DBUtilsRepositoryFactoryTest {
    @Test
    public void test1() {
        JpaRepositoryFactory jpaRepositoryFactory = new DBUtilsRepositoryFactory();
        JpaRepository<Student, Integer> repository = jpaRepositoryFactory.createRepository(StudentRepository.class);

        Student student = new Student(0, "DBUtilsRepositoryFactory", "DBUtilsRepositoryFactory@qq.com");
        repository.save(student);
    }
}
