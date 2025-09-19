/**
 * BookRepository 인터페이스
 * ------------------------------
 * - 도서(Book) 엔티티에 대한 데이터 접근 계층(Repository) 정의
 * - 구현체는 메모리, DB, 파일 등 다양한 저장소에 따라 달라질 수 있음
 * - 기본적인 CRUD 성격의 메서드와 도메인 특화 메서드 제공
 * File Path : library/repository/BookRepository.java
 */
package com.example.library.repository;

import com.example.library.model.Book;
import java.util.Optional;
import java.util.List;

public interface BookRepository {
    
    /**
     * 도서 저장
     * - 신규 도서 등록 또는 기존 도서 갱신
     * 
     * @param book 저장할 Book 객체
     * @return 저장된 Book 객체 (DB/저장소에서 확정된 상태)
     */
    Book save(Book book);

    /**
     * ISBN 기준으로 도서 조회
     * 
     * @param isbn 조회할 도서의 고유 ISBN
     * @return 해당 ISBN 의 도서(Optional) 
     *         → 존재하지 않으면 Optional.empty()
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * 모든 도서 조회
     * 
     * @return 저장소에 있는 전체 도서 목록
     */
    List<Book> findAll();

    /**
     * 현재 대출 가능한 도서 목록 조회
     * - Book.isAvailable() == true 인 도서만 반환
     * 
     * @return 대출 가능한 도서 리스트
     */
    List<Book> findAvailableBooks();
}
