/**
 * MemberRepository 인터페이스
 * ------------------------------
 * - 도서관 회원(Member) 엔티티에 대한 데이터 접근 계층 정의
 * - 회원 등록, 조회, 전체 회원 목록 조회 기능 제공
 * - 구현체는 메모리/DB/파일 기반으로 다양하게 작성될 수 있음
 * File Path : library/repository/MemberRepository.java
 */
package com.example.library.repository;

import com.example.library.model.Member;
import java.util.Optional;
import java.util.List;

public interface MemberRepository {
    
    /**
     * 회원 저장
     * - 신규 회원 등록 또는 기존 회원 갱신
     *
     * @param member 저장할 Member 객체
     * @return 저장된 Member 객체
     */
    Member save(Member member);

    /**
     * 회원 ID로 회원 조회
     *
     * @param memberId 조회할 회원의 고유 ID
     * @return 해당 회원(Optional), 존재하지 않으면 Optional.empty()
     */
    Optional<Member> findById(String memberId);

    /**
     * 모든 회원 목록 조회
     *
     * @return 전체 회원 리스트
     */
    List<Member> findAll();
}
