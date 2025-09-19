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
        if (!member.canBorrow()) {
            throw new IllegalStateException("대출 한도 초과");
        }
        member.borrowBook();
        book.borrow();
    }

    public void processReturn(LocalDate returnDate) {
        if (returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("대출일 이전 반납 불가");
        }
        this.returnDate = returnDate;
        member.returnBook();
        book.returnBook();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
