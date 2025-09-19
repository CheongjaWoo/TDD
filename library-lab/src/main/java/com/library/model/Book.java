package com.library.model;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean available = true;

    public Book(String title, String author, String isbn) {
        // TODO: title, author, isbn 유효성 검사
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public void borrow() {
        // TODO: available이 true일 때만 false로 변경, 아니면 예외 발생
    }

    public void returnBook() {
        // TODO: available이 false일 때만 true로 변경, 아니면 예외 발생
    }

    public boolean isAvailable() {
        return available;
    }
}
