package com.example.books;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private final String id;
    private final String userId;
    private final String isbn;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    
    public Loan(String id, String userId, String isbn, LocalDate loanDate, int loanPeriodDays) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("대출 ID는 필수입니다");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("도서 ISBN은 필수입니다");
        }
        if (loanDate == null) {
            throw new IllegalArgumentException("대출일은 필수입니다");
        }
        if (loanPeriodDays <= 0) {
            throw new IllegalArgumentException("대출 기간은 1일 이상이어야 합니다");
        }
        
        this.id = id;
        this.userId = userId;
        this.isbn = isbn;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(loanPeriodDays);
        this.status = LoanStatus.ACTIVE;
    }
    
    public void returnBook(LocalDate returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException("반납일은 필수입니다");
        }
        if (returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("반납일은 대출일 이후여야 합니다");
        }
        
        this.returnDate = returnDate;
        this.status = LoanStatus.RETURNED;
    }
    
    public boolean isOverdue(LocalDate currentDate) {
        return status == LoanStatus.ACTIVE && currentDate.isAfter(dueDate);
    }
    
    public long getOverdueDays(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return currentDate.toEpochDay() - dueDate.toEpochDay();
    }
    
    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public LoanStatus getStatus() { return status; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}