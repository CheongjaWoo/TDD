/**
 * Member 클래스
 * ------------------------------
 * - 도서관 회원을 표현하는 모델 클래스
 * - 회원 ID, 이름, 현재 대출 중인 도서 권수를 관리
 * - 최대 대출 권수 제한(MAX_BORROW_LIMIT)을 적용
 * * File Path : library/model/Member.java
 */
package com.example.library.model;

public class Member {
    // 회원 1명이 동시에 대출할 수 있는 최대 권수
    private static final int MAX_BORROW_LIMIT = 3;
    
    private final String memberId;        // 회원 고유 ID
    private final String name;            // 회원 이름
    private int borrowedBooksCount;       // 현재 대출 중인 도서 권수
    
    /**
     * Member 생성자
     * - memberId와 name은 필수 값이며, null/빈 값일 경우 예외 발생
     * - 처음 생성 시 borrowedBooksCount = 0
     *
     * @param memberId 회원 고유 ID
     * @param name     회원 이름
     */
    public Member(String memberId, String name) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원ID는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다");
        }
        
        this.memberId = memberId.trim();
        this.name = name.trim();
        this.borrowedBooksCount = 0; // 초기 대출 권수는 0
    }
    
    // ===== Getter 메서드 =====
    public String getMemberId() {
        return memberId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }
    
    /**
     * 현재 회원이 추가로 책을 빌릴 수 있는지 확인
     * @return true → 대출 가능 / false → 대출 불가
     */
    public boolean canBorrow() {
        return borrowedBooksCount < MAX_BORROW_LIMIT;
    }
    
    /**
     * 책을 대출할 때 호출
     * - canBorrow() 가 false이면 예외 발생
     * - borrowedBooksCount 1 증가
     */
    public void borrowBook() {
        if (!canBorrow()) {
            throw new IllegalStateException("대출 한도를 초과했습니다 (최대 " + MAX_BORROW_LIMIT + "권)");
        }
        this.borrowedBooksCount++;
    }
    
    /**
     * 책을 반납할 때 호출
     * - 현재 대출 권수가 0이면 반납 불가 (예외 발생)
     * - borrowedBooksCount 1 감소
     */
    public void returnBook() {
        if (borrowedBooksCount == 0) {
            throw new IllegalStateException("반납할 도서가 없습니다");
        }
        this.borrowedBooksCount--;
    }
}
