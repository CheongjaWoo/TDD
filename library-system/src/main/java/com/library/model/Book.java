package com.library.model;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean available = true;

    public Book(String title, String author, String isbn) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("제목 필수");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("저자 필수");
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN 필수");
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public void borrow() {
        if (!available) throw new IllegalStateException("이미 대출중인 책");
        available = false;
    }

    public void returnBook() {
        if (available) throw new IllegalStateException("대출되지 않은 책은 반납 불가");
        available = true;
    }

    public boolean isAvailable() {
        return available;
    }
}
