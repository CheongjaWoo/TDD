/**
 * MemberNotFoundException
 * -------------------------------------
 * - 도서관 시스템에서 특정 회원(Member)을 찾을 수 없을 때 발생하는 예외 클래스
 * - RuntimeException을 상속 → 언체크 예외(Unchecked Exception)
 *   → 호출하는 쪽에서 반드시 try-catch로 처리할 필요는 없음
 *
 * 사용 예:
 *   Member member = repository.findById(memberId)
 *           .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다. ID=" + memberId));
 * File Path: library/exception/MemberNotFoundException.java
 */
package com.example.library.exception;

public class MemberNotFoundException extends RuntimeException {

    /**
     * 단순 메시지를 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     */
    public MemberNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 메시지와 실제 원인(cause)을 함께 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     * @param cause   중첩 예외(실제 발생한 예외)
     */
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
