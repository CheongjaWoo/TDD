package com.example.books.service;

import com.example.books.Book;
import com.example.books.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public void register(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateBookException("이미 등록된 도서입니다: " + book.getIsbn());
        }
        bookRepository.save(book);
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN이 필요합니다");
        }
        return bookRepository.findByIsbn(isbn);
    }
    
    public List<Book> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 제목이 필요합니다");
        }
        return bookRepository.findByTitle(title);
    }
    
    public List<Book> searchByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 저자명이 필요합니다");
        }
        return bookRepository.findByAuthor(author);
    }
    
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    public void removeBook(String isbn) {
        if (!bookRepository.existsByIsbn(isbn)) {
            throw new BookNotFoundException("존재하지 않는 도서입니다: " + isbn);
        }
        bookRepository.deleteByIsbn(isbn);
    }
}