package com.library.model;

import java.time.LocalDate;

public class Loan {
    private Member member;
    private Book book;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(Member member, Book book, LocalDate loanDate) {
        this.member = member;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14);
    }

    public void processBorrow() {
        // TODO: member.canBorrow()가 true일 때만 대출 처리
        // member.borrowBook();
        // book.borrow();
        // false면 예외 발생
    }

    public void processReturn(LocalDate returnDate) {
        // TODO: 반납일이 loanDate 이전이면 예외 발생
        // member.returnBook();
        // book.returnBook();
        this.returnDate = returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
