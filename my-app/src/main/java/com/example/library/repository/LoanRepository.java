/**
 * LoanRepository 인터페이스
 * ------------------------------
 * - 도서 대출(Loan) 엔티티에 대한 데이터 접근 계층 정의
 * - 회원(Member)별 대출 조회, 대출 상태(활성/반납 여부) 기반 조회 기능 제공
 * - 구현체는 메모리, DB 등 다양한 저장소 방식으로 작성될 수 있음
 * File Path : library/repository/LoanRepository.java
 */
package com.example.library.repository;

import com.example.library.model.Loan;
import com.example.library.model.Member;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    
    /**
     * 대출 정보 저장
     * - 신규 대출 등록 또는 기존 대출 갱신
     *
     * @param loan 저장할 Loan 객체
     * @return 저장된 Loan 객체
     */
    Loan save(Loan loan);

    /**
     * 특정 회원이 대출한 내역 조회
     *
     * @param member 대출자(Member)
     * @return 해당 회원이 대출한 Loan 리스트
     */
    List<Loan> findByMember(Member member);

    /**
     * 현재 반납되지 않은 모든 대출 내역 조회
     *
     * @return 활성 대출(Active Loan) 리스트
     */
    List<Loan> findActiveLoans();

    /**
     * 특정 도서(ISBN)의 활성 대출 건 조회
     * - 해당 도서가 아직 반납되지 않은 경우 조회 가능
     *
     * @param isbn 도서의 고유 ISBN
     * @return 활성 Loan(Optional), 없으면 Optional.empty()
     */
    Optional<Loan> findActiveLoanByBookIsbn(String isbn);
}
