package com.library.model;

public class Member {
    private String memberId;
    private String name;
    private int borrowedBooksCount = 0;
    private static final int MAX_BORROW = 3;

    public Member(String memberId, String name) {
        if (memberId == null || memberId.isBlank()) throw new IllegalArgumentException("회원 ID 필수");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름 필수");
        this.memberId = memberId;
        this.name = name;
    }

    public void borrowBook() {
        if (borrowedBooksCount >= MAX_BORROW) {
            throw new IllegalStateException("대출 한도 초과");
        }
        borrowedBooksCount++;
    }

    public void returnBook() {
        if (borrowedBooksCount <= 0) {
            throw new IllegalStateException("반납할 책이 없음");
        }
        borrowedBooksCount--;
    }

    public boolean canBorrow() {
        return borrowedBooksCount < MAX_BORROW;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }
}
