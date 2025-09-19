package com.example.books;

import java.util.Objects;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    
    public Book(String isbn, String title, String author) {
        validateIsbn(isbn);
        validateTitle(title);
        validateAuthor(author);
        
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
    
    private void validateIsbn(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN은 필수입니다");
        }
        if (isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 ISBN입니다");
        }
        
        // 더 엄격한 ISBN 검증
        String cleanIsbn = isbn.replaceAll("[^0-9X]", ""); // 숫자와 X만 남김
        
        // 길이 검증 (ISBN-10: 10자리, ISBN-13: 13자리)
        if (cleanIsbn.length() != 10 && cleanIsbn.length() != 13) {
            throw new IllegalArgumentException("유효하지 않은 ISBN입니다");
        }
        
        // ISBN-13은 978 또는 979로 시작해야 함
        if (cleanIsbn.length() == 13) {
            if (!cleanIsbn.startsWith("978") && !cleanIsbn.startsWith("979")) {
                throw new IllegalArgumentException("유효하지 않은 ISBN입니다");
            }
        }
        
        // 기본적인 형식 검증
        if (!cleanIsbn.matches("^[0-9]{9}[0-9X]$") && !cleanIsbn.matches("^[0-9]{13}$")) {
            throw new IllegalArgumentException("유효하지 않은 ISBN입니다");
        }
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다");
        }
    }
    
    private void validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("저자는 필수입니다");
        }
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s'}", 
                           isbn, title, author);
    }
}