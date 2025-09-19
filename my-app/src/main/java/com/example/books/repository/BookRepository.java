package com.example.books.repository;

import com.example.books.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    void save(Book book);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findAll();
    boolean existsByIsbn(String isbn);
    void deleteByIsbn(String isbn);
}

