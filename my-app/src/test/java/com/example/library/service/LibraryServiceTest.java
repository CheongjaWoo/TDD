// src/test/java/com/example/library/service/LibraryServiceTest.java
package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Loan;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.exception.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LibraryServiceTest 클래스
 * ------------------------------
 * - LibraryService 의 핵심 시나리오를 단위 테스트로 검증
 * - Mockito 를 사용하여 Repository 의존성을 Mock 처리
 * - 주요 테스트 시나리오:
 *   1) 도서 대출 성공 / 실패
 *   2) 도서 반납 성공
 *   3) 연체료 계산
 */
class LibraryServiceTest {

    // ===== Mock Repository 의존성 =====
    @Mock private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private LoanRepository loanRepository;

    private LibraryService libraryService; // 테스트 대상 (SUT: System Under Test)

    // 테스트용 더미 데이터
    private Book book;
    private Member member;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(bookRepository, memberRepository, loanRepository);

        // 기본 테스트 데이터
        book = new Book("테스트 책", "저자", "ISBN-001");
        member = new Member("M001", "홍길동");

        // DisplayName 콘솔 출력
        System.out.println("\n▶ [테스트 시작]: " + testInfo.getDisplayName());
    }

    // ===== 테스트 시나리오 =====

    @Test
    @DisplayName("✅ 도서 대출 성공 시나리오")
    void testBorrowBook_Success() {
        // Given: 저장소에서 도서와 회원 정상 조회
        when(bookRepository.findByIsbn("ISBN-001")).thenReturn(Optional.of(book));
        when(memberRepository.findById("M001")).thenReturn(Optional.of(member));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        // When: 도서 대출 실행
        Loan loan = libraryService.borrowBook("ISBN-001", "M001", LocalDate.now());

        // Then: Loan 생성 및 상태 검증
        assertNotNull(loan);
        assertEquals("ISBN-001", loan.getBook().getIsbn());
        assertEquals("M001", loan.getMember().getMemberId());
        assertFalse(book.isAvailable()); // 도서 상태 변경 확인
        assertEquals(1, member.getBorrowedBooksCount()); // 회원 대출 권수 확인
    }

    @Test
    @DisplayName("❌ 도서 대출 실패 - 도서 없음")
    void testBorrowBook_BookNotFound() {
        // Given: ISBN 조회 결과 없음
        when(bookRepository.findByIsbn("UNKNOWN")).thenReturn(Optional.empty());

        // Then: 예외 발생 확인
        assertThrows(BookNotFoundException.class,
                () -> libraryService.borrowBook("UNKNOWN", "M001", LocalDate.now()));
    }

    @Test
    @DisplayName("❌ 도서 대출 실패 - 회원 없음")
    void testBorrowBook_MemberNotFound() {
        // Given: 도서는 존재하지만 회원 없음
        when(bookRepository.findByIsbn("ISBN-001")).thenReturn(Optional.of(book));
        when(memberRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        // Then: 예외 발생 확인
        assertThrows(MemberNotFoundException.class,
                () -> libraryService.borrowBook("ISBN-001", "UNKNOWN", LocalDate.now()));
    }

    @Test
    @DisplayName("❌ 도서 대출 실패 - 이미 대출된 도서")
    void testBorrowBook_BookNotAvailable() {
        // Given: 도서가 이미 대출 상태
        book.borrow();
        when(bookRepository.findByIsbn("ISBN-001")).thenReturn(Optional.of(book));
        when(memberRepository.findById("M001")).thenReturn(Optional.of(member));

        // Then: 예외 발생 확인
        assertThrows(BookNotAvailableException.class,
                () -> libraryService.borrowBook("ISBN-001", "M001", LocalDate.now()));
    }

    @Test
    @DisplayName("❌ 도서 대출 실패 - 회원 대출 한도 초과")
    void testBorrowBook_MemberLimitExceeded() {
        // Given: 회원이 이미 3권 대출 중
        member.borrowBook();
        member.borrowBook();
        member.borrowBook();
        when(bookRepository.findByIsbn("ISBN-001")).thenReturn(Optional.of(book));
        when(memberRepository.findById("M001")).thenReturn(Optional.of(member));

        // Then: 예외 발생 확인
        assertThrows(MemberBorrowLimitExceededException.class,
                () -> libraryService.borrowBook("ISBN-001", "M001", LocalDate.now()));
    }

    @Test
    @DisplayName("✅ 도서 반납 성공 시나리오")
    void testReturnBook_Success() {
        // Given: 대출 중인 Loan 존재
        Loan loan = new Loan(book, member, LocalDate.now().minusDays(10));
        when(loanRepository.findActiveLoanByBookIsbn("ISBN-001")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        // 도서와 회원을 대출 상태로 설정
        book.borrow();
        member.borrowBook();

        // When: 반납 실행
        Loan returned = libraryService.returnBook("ISBN-001", LocalDate.now());

        // Then: 반납 상태 검증
        assertTrue(returned.isReturned());
        assertTrue(book.isAvailable());
        assertEquals(0, member.getBorrowedBooksCount());
    }

    @Test
    @DisplayName("✅ 연체료 계산 - 연체 없음 (0원)")
    void testCalculateLateFee_NoOverdue() {
        // Given: 대출일이 5일 전 (아직 기한 이내)
        Loan loan = new Loan(book, member, LocalDate.now().minusDays(5));
        when(loanRepository.findActiveLoanByBookIsbn("ISBN-001")).thenReturn(Optional.of(loan));

        // When
        int fee = libraryService.calculateLateFee("ISBN-001", LocalDate.now());

        // Then
        assertEquals(0, fee);
    }

    @Test
    @DisplayName("✅ 연체료 계산 - 6일 연체 (600원)")
    void testCalculateLateFee_Overdue() {
        // Given: 대출일이 20일 전 → 14일 기한 초과 → 6일 연체
        Loan loan = new Loan(book, member, LocalDate.now().minusDays(20));
        when(loanRepository.findActiveLoanByBookIsbn("ISBN-001")).thenReturn(Optional.of(loan));

        // When
        int fee = libraryService.calculateLateFee("ISBN-001", LocalDate.now());

        // Then
        assertEquals(600, fee); // 100원 × 6일
    }
}
