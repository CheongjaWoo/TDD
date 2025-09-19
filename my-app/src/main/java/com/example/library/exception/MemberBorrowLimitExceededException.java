
/**
 * MemberBorrowLimitExceededException
 * -------------------------------------
 * - 도서관 시스템에서 회원(Member)이 허용된 대출 권수를 초과하여 책을 빌리려 할 때 발생하는 예외 클래스
 * - RuntimeException을 상속 → 언체크 예외(Unchecked Exception)
 *   → 반드시 try-catch로 처리하지 않아도 되며, 서비스 계층에서 발생시켜 컨트롤러/상위 계층에서 처리 가능
 *
 * 사용 예:
 *   if (member.getBorrowedBooks().size() >= member.getBorrowLimit()) {
 *       throw new MemberBorrowLimitExceededException("대출 가능 권수를 초과했습니다.");
 *   }
 *  File Path: library/exception/MemberBorrowLimitExceededException.java
 */
package com.example.library.exception;

public class MemberBorrowLimitExceededException extends RuntimeException {

    /**
     * 단순 메시지를 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     */
    public MemberBorrowLimitExceededException(String message) {
        super(message);
    }
    
    /**
     * 메시지와 실제 원인(cause)을 함께 전달하는 생성자
     * @param message 예외 원인을 설명하는 메시지
     * @param cause 중첩 예외(실제 발생한 예외)
     */
    public MemberBorrowLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
