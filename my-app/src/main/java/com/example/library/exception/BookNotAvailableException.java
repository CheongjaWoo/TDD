/**
 * BookNotAvailableException
 * -------------------------------------
 * - 라이브러리/도서관 시스템에서 "책을 대출할 수 없는 경우" 발생시키는 예외 클래스
 * - RuntimeException을 상속받아, 체크 예외(Checked Exception)가 아닌 언체크 예외(Unchecked Exception)로 동작
 *   → 호출 메서드에서 try-catch 로 반드시 처리하지 않아도 됨
 *
 * 사용 예:
 *   if (!book.isAvailable()) {
 *       throw new BookNotAvailableException("해당 도서는 현재 대출 중입니다.");
 *   }
 *  File Path : library/exception/BookNotAvailableException.java
 */
package com.example.library.exception;

public class BookNotAvailableException extends RuntimeException {

    /**
     * 단순 메시지를 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     */
    public BookNotAvailableException(String message) {
        super(message);
    }
    
    /**
     * 메시지와 원인(cause)을 함께 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     * @param cause 실제 예외 원인(중첩 예외)
     */
    public BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
