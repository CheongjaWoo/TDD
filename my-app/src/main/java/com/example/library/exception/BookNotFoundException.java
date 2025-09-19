/**
 * BookNotFoundException
 * -------------------------------------
 * - 도서관 시스템에서 특정 책(Book)을 찾을 수 없을 때 발생시키는 예외 클래스
 * - RuntimeException을 상속받아 "언체크 예외(Unchecked Exception)"로 동작
 *   → 호출하는 쪽에서 반드시 예외 처리를 강제하지 않음
 *
 * 사용 예:
 *   Book book = repository.findById(bookId)
 *           .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다: " + bookId));
 * File Path : library/exception/BookNotFoundException.java
 */
package com.example.library.exception;

public class BookNotFoundException extends RuntimeException {

    /**
     * 단순 메시지를 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     */
    public BookNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 메시지와 실제 원인(cause)을 함께 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     * @param cause 중첩 예외(실제 발생한 예외)
     */
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
