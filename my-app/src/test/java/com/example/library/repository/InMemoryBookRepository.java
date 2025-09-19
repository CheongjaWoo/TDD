// ===== InMemory Repository 구현체 (테스트용) =====

// src/test/java/com/library/repository/InMemoryBookRepository.java
package com.example.library.repository;

import com.example.library.model.Book;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    
    @Override
    public Book save(Book book) {
        books.put(book.getIsbn(), book);
        return book;
    }
    
    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
    
    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
    
    @Override
    public List<Book> findAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    public void clear() {
        books.clear();
    }
    
    public int size() {
        return books.size();
    }
}