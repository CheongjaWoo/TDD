package com.example.books.repository;

import com.example.books.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    boolean existsById(String id);
}