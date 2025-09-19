/**
 * Loan 클래스
 * ------------------------------
 * - 한 회원(Member)이 특정 도서(Book)를 대출한 내역을 표현
 * - 대출일, 반납 예정일, 실제 반납일을 관리
 * - 연체 여부 및 연체료 계산 기능 포함
 * File Path : library/model/Loan.java
 */
package com.example.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan { 
    private static final int LOAN_PERIOD_DAYS = 14; // 기본 대출 기간 (14일)
    private static final int LATE_FEE_PER_DAY = 100; // 연체료 (하루당 100원)
    
    private final Book book;          // 대출된 도서
    private final Member member;      // 대출자(회원)
    private final LocalDate loanDate; // 대출일
    private final LocalDate dueDate;  // 반납 예정일 = 대출일 + LOAN_PERIOD_DAYS
    private LocalDate returnDate;     // 실제 반납일 (반납 전까지 null)
    
    /**
     * Loan 생성자
     * - Book, Member, LoanDate 는 필수 값
     * - 반납 예정일은 대출일로부터 14일 뒤 자동 계산
     * - 처음 생성 시 returnDate 는 null (반납 전)
     */
    public Loan(Book book, Member member, LocalDate loanDate) {
        if (book == null) {
            throw new IllegalArgumentException("도서는 필수입니다");
        }
        if (member == null) {
            throw new IllegalArgumentException("회원은 필수입니다");
        }
        if (loanDate == null) {
            throw new IllegalArgumentException("대출일은 필수입니다");
        }
        
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(LOAN_PERIOD_DAYS); // 대출일 + 14일
        this.returnDate = null; // 아직 반납되지 않음
    }
    
    // ===== Getter 메서드 =====
    public Book getBook() {
        return book;
    }
    
    public Member getMember() {
        return member;
    }
    
    public LocalDate getLoanDate() {
        return loanDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    /**
     * 반납 여부 확인
     * @return returnDate 가 null 이 아니면 true
     */
    public boolean isReturned() {
        return returnDate != null;
    }
    
    /**
     * 도서 반납 처리
     * - 이미 반납된 경우 IllegalStateException 발생
     * - 반납일이 대출일 이전이면 IllegalArgumentException 발생
     * @param returnDate 실제 반납일
     */
    public void returnBook(LocalDate returnDate) {
        if (isReturned()) {
            throw new IllegalStateException("이미 반납된 도서입니다");
        }
        if (returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("반납일은 대출일 이후여야 합니다");
        }
        
        this.returnDate = returnDate;
    }
    
    /**
     * 특정 날짜 기준으로 연체 여부 확인
     * @param checkDate 확인할 날짜
     * @return checkDate 가 dueDate 이후면 true
     */
    public boolean isOverdue(LocalDate checkDate) {
        return checkDate.isAfter(dueDate);
    }
    
    /**
     * 연체료 계산
     * - 반환일(returnDate)이 예정일을 초과했을 경우 초과일 × 100원
     * - 초과하지 않으면 0원
     * @param returnDate 실제 반납일
     * @return 연체료 금액
     */
    public int calculateLateFee(LocalDate returnDate) {
        if (!isOverdue(returnDate)) {
            return 0; // 연체 아님
        }
        
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate); // 연체 일수 계산
        return (int) (overdueDays * LATE_FEE_PER_DAY);
    }
}
