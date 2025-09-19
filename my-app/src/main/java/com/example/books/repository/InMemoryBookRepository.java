package com.example.books.repository;

import com.example.books.Book;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    
    @Override
    public void save(Book book) {
        books.put(book.getIsbn(), book);
    }
    
    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
    
    @Override
    public List<Book> findByTitle(String title) {
        return books.values().stream()
            .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findByAuthor(String author) {
        return books.values().stream()
            .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
    
    @Override
    public boolean existsByIsbn(String isbn) {
        return books.containsKey(isbn);
    }
    
    @Override
    public void deleteByIsbn(String isbn) {
        books.remove(isbn);
    }
    
    // 테스트용 메서드
    public void clear() {
        books.clear();
    }
    
    public int size() {
        return books.size();
    }
}