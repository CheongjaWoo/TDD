package com.library.model;

public class Member {
    private String memberId;
    private String name;
    private int borrowedBooksCount = 0;
    private static final int MAX_BORROW = 3;

    public Member(String memberId, String name) {
        // TODO: memberId, name 유효성 검사 (null/빈문자 불가)
        this.memberId = memberId;
        this.name = name;
    }

    public void borrowBook() {
        // TODO: borrowedBooksCount 증가 (단, 한도 초과 시 예외 발생)
    }

    public void returnBook() {
        // TODO: borrowedBooksCount 감소 (단, 0보다 작아질 수 없음)
    }

    public boolean canBorrow() {
        // TODO: 현재 count < MAX_BORROW 인지 반환
        return false;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }
}
