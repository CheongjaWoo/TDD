/**
 * Book 클래스
 * ------------------------------
 * - 도서관 시스템에서 책(도서)을 표현하는 모델 클래스
 * - title, author, isbn은 불변(immutable) 값
 * - available 필드를 통해 현재 대출 가능 여부 관리
 * File Path : library/model/Book.java
 */
package com.example.library.model;

public class Book {
    private final String title;  // 책 제목
    private final String author; // 책 저자
    private final String isbn;   // ISBN (고유 식별자)
    private boolean available;   // true → 대출 가능, false → 대출 중
    
    /**
     * Book 생성자
     * - title, author, isbn 값이 비어있으면 예외 발생
     * - 생성 시 기본적으로 available = true (대출 가능 상태)
     *
     * @param title  책 제목 (필수)
     * @param author 책 저자 (필수)
     * @param isbn   책의 고유 번호 (필수)
     */
    public Book(String title, String author, String isbn) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("저자는 필수입니다");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN은 필수입니다");
        }
        
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true; // 생성 시 기본 상태는 대출 가능
    }
    
    // Getter 메서드들
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * 도서를 대출 처리
     * - 이미 대출된 상태면 IllegalStateException 발생
     */
    public void borrow() {
        if (!available) {
            throw new IllegalStateException("이미 대출된 도서입니다");
        }
        this.available = false; // 대출 상태로 변경
    }
    
    /**
     * 도서를 반납 처리
     * - 대출되지 않은 상태면 IllegalStateException 발생
     */
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("대출되지 않은 도서입니다");
        }
        this.available = true; // 다시 대출 가능 상태로 변경
    }
}
