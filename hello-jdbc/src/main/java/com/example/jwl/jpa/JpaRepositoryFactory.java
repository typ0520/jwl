package com.example.jwl.jpa;

/**
 * @author tong
 */
public interface JpaRepositoryFactory {
    <T, ID> JpaRepository<T, ID> createRepository(Class<? extends JpaRepository<T, ID>> repoClass);
}
