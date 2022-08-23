package com.example.jwl.jpa;

import com.example.jwl.Student;

public interface JpaRepository<T, ID> {
    void save(T t);

    boolean update(T t);

    Student findById(ID id);

    boolean delete(T t);
}