// src/test/java/com/library/service/LibraryServiceReTest.java
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LibraryService 테스트 클래스 (리팩토링 버전)
 * ===============================================
 * - TDD Red-Green-Refactor 패턴 적용
 * - Given-When-Then 구조로 테스트 작성
 * - 테스트 헬퍼 메서드로 코드 중복 제거
 * - 의미있는 테스트 데이터 사용
 * - 각 테스트마다 DisplayName을 콘솔에 출력
 */
@DisplayName("📚 도서관 서비스 테스트")
class LibraryServiceReTest {

    // ===== Test Fixtures =====
    @Mock private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private LoanRepository loanRepository;

    private LibraryService libraryService;

    // ===== Test Data Constants =====
    private static final String VALID_ISBN = "978-89-123-4567-8";
    private static final String INVALID_ISBN = "UNKNOWN-ISBN";
    private static final String VALID_MEMBER_ID = "M20250001";
    private static final String INVALID_MEMBER_ID = "UNKNOWN-MEMBER";
    private static final LocalDate BASE_DATE = LocalDate.of(2025, 1, 15);
    private static final int MEMBER_BORROW_LIMIT = 3;

    // ===== Test Objects =====
    private Book availableBook;
    private Book borrowedBook;
    private Member normalMember;
    private Member limitExceededMember;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        // 📌 DisplayName 출력
        System.out.println("\n🚀 " + testInfo.getDisplayName());
        
        // Mock 객체 초기화
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(bookRepository, memberRepository, loanRepository);

        // 테스트 데이터 초기화
        initializeTestData();

        // DisplayName 콘솔 출력
        System.out.println("\n▶ [테스트 시작]: " + testInfo.getDisplayName());
    }

    // ===== 도서 대출 테스트 =====
    
    @Nested
    @DisplayName("📖 도서 대출 기능")
    class BorrowBookTest {

        @Test
        @DisplayName("✅ 정상적인 도서 대출이 성공한다")
        void shouldSuccessfullyBorrowBook() {
            // Given: 대출 가능한 도서와 정상 회원이 준비됨
            givenBookExists(VALID_ISBN, availableBook);
            givenMemberExists(VALID_MEMBER_ID, normalMember);
            givenLoanCanBeSaved();

            // When: 도서 대출을 요청함
            Loan result = libraryService.borrowBook(VALID_ISBN, VALID_MEMBER_ID, BASE_DATE);

            // Then: 대출이 성공적으로 처리됨
            assertThat(result).isNotNull();
            assertThat(result.getBook()).isEqualTo(availableBook);
            assertThat(result.getMember()).isEqualTo(normalMember);
            assertThat(result.getLoanDate()).isEqualTo(BASE_DATE);
            
            // 도서와 회원 상태가 정상적으로 변경됨
            assertThat(availableBook.isAvailable()).isFalse();
            assertThat(normalMember.getBorrowedBooksCount()).isEqualTo(1);
            
            // 저장 메서드들이 호출됨
            verifyAllRepositoriesSaved();
        }

        @Test
        @DisplayName("❌ 존재하지 않는 도서 대출 시 예외가 발생한다")
        void shouldThrowExceptionWhenBookNotFound() {
            // Given: 존재하지 않는 도서
            givenBookNotExists(INVALID_ISBN);

            // When & Then: 예외가 발생함
            assertThatThrownBy(() -> 
                libraryService.borrowBook(INVALID_ISBN, VALID_MEMBER_ID, BASE_DATE))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("도서를 찾을 수 없습니다: " + INVALID_ISBN);
        }

        @Test
        @DisplayName("❌ 존재하지 않는 회원 대출 시 예외가 발생한다")
        void shouldThrowExceptionWhenMemberNotFound() {
            // Given: 존재하는 도서, 존재하지 않는 회원
            givenBookExists(VALID_ISBN, availableBook);
            givenMemberNotExists(INVALID_MEMBER_ID);

            // When & Then: 예외가 발생함
            assertThatThrownBy(() -> 
                libraryService.borrowBook(VALID_ISBN, INVALID_MEMBER_ID, BASE_DATE))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다: " + INVALID_MEMBER_ID);
        }

        @Test
        @DisplayName("❌ 이미 대출된 도서 대출 시 예외가 발생한다")
        void shouldThrowExceptionWhenBookNotAvailable() {
            // Given: 이미 대출된 도서
            givenBookExists(VALID_ISBN, borrowedBook);
            givenMemberExists(VALID_MEMBER_ID, normalMember);

            // When & Then: 예외가 발생함
            assertThatThrownBy(() -> 
                libraryService.borrowBook(VALID_ISBN, VALID_MEMBER_ID, BASE_DATE))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("대출 불가능한 도서입니다");
        }

        @Test
        @DisplayName("❌ 대출 한도를 초과한 회원은 대출할 수 없다")
        void shouldThrowExceptionWhenMemberExceedsLimit() {
            // Given: 대출 한도를 초과한 회원
            givenBookExists(VALID_ISBN, availableBook);
            givenMemberExists(VALID_MEMBER_ID, limitExceededMember);

            // When & Then: 예외가 발생함
            assertThatThrownBy(() -> 
                libraryService.borrowBook(VALID_ISBN, VALID_MEMBER_ID, BASE_DATE))
                .isInstanceOf(MemberBorrowLimitExceededException.class)
                .hasMessageContaining("대출 한도를 초과했습니다");
        }
    }

    // ===== 도서 반납 테스트 =====
    
    @Nested
    @DisplayName("📤 도서 반납 기능")
    class ReturnBookTest {

        @Test
        @DisplayName("✅ 정상적인 도서 반납이 성공한다")
        void shouldSuccessfullyReturnBook() {
            // Given: 대출 중인 도서가 존재함
            Loan activeLoan = createActiveLoan();
            givenActiveLoanExists(VALID_ISBN, activeLoan);
            givenLoanCanBeSaved();

            // When: 도서 반납을 요청함
            LocalDate returnDate = BASE_DATE.plusDays(5);
            Loan result = libraryService.returnBook(VALID_ISBN, returnDate);

            // Then: 반납이 성공적으로 처리됨
            assertThat(result.isReturned()).isTrue();
            assertThat(result.getReturnDate()).isEqualTo(returnDate);
            assertThat(result.getBook().isAvailable()).isTrue();
            assertThat(result.getMember().getBorrowedBooksCount()).isEqualTo(0);
            
            // 저장 메서드들이 호출됨
            verifyAllRepositoriesSaved();
        }

        @Test
        @DisplayName("❌ 활성 대출이 없는 도서 반납 시 예외가 발생한다")
        void shouldThrowExceptionWhenNoActiveLoan() {
            // Given: 활성 대출이 없는 도서
            givenActiveLoanNotExists(VALID_ISBN);

            // When & Then: 예외가 발생함
            assertThatThrownBy(() -> 
                libraryService.returnBook(VALID_ISBN, BASE_DATE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("해당 도서의 활성 대출을 찾을 수 없습니다");
        }
    }

    // ===== 연체료 계산 테스트 =====
    
    @Nested
    @DisplayName("💰 연체료 계산 기능")
    class CalculateLateFeeTest {

        @Test
        @DisplayName("✅ 연체되지 않은 도서는 연체료가 0원이다")
        void shouldReturnZeroFeeWhenNotOverdue() {
            // Given: 연체되지 않은 대출 (5일 전 대출, 대출 기간 14일)
            Loan nonOverdueLoan = new Loan(availableBook, normalMember, BASE_DATE.minusDays(5));
            givenActiveLoanExists(VALID_ISBN, nonOverdueLoan);

            // When: 연체료를 계산함
            int lateFee = libraryService.calculateLateFee(VALID_ISBN, BASE_DATE);

            // Then: 연체료가 0원임
            assertThat(lateFee).isEqualTo(0);
        }

        @Test
        @DisplayName("✅ 6일 연체된 도서의 연체료는 600원이다")
        void shouldCalculateCorrectFeeForOverdueBook() {
            // Given: 6일 연체된 대출 (20일 전 대출, 대출 기간 14일)
            Loan overdueLoan = new Loan(availableBook, normalMember, BASE_DATE.minusDays(20));
            givenActiveLoanExists(VALID_ISBN, overdueLoan);

            // When: 연체료를 계산함
            int lateFee = libraryService.calculateLateFee(VALID_ISBN, BASE_DATE);

            // Then: 연체료가 정확히 계산됨 (6일 × 100원 = 600원)
            assertThat(lateFee).isEqualTo(600);
        }
    }

    // ===== 회원 대출 목록 조회 테스트 =====
    
    @Nested
    @DisplayName("👤 회원 대출 목록 조회")
    class GetMemberLoansTest {

        @Test
        @DisplayName("✅ 회원의 대출 목록을 정상적으로 조회한다")
        void shouldReturnMemberLoans() {
            // Given: 회원이 존재하고 대출 목록이 있음
            List<Loan> expectedLoans = createMemberLoans();
            givenMemberExists(VALID_MEMBER_ID, normalMember);
            when(loanRepository.findByMember(normalMember)).thenReturn(expectedLoans);

            // When: 회원의 대출 목록을 조회함
            List<Loan> result = libraryService.getMemberLoans(VALID_MEMBER_ID);

            // Then: 올바른 대출 목록이 반환됨
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedLoans);
        }
    }

    // ===== 연체 도서 목록 조회 테스트 =====
    
    @Nested
    @DisplayName("⏰ 연체 도서 목록 조회")
    class GetOverdueBooksTest {

        @Test
        @DisplayName("✅ 연체된 도서들만 필터링하여 반환한다")
        void shouldReturnOnlyOverdueBooks() {
            // Given: 활성 대출 목록 (연체된 것과 안 된 것 혼재)
            List<Loan> allActiveLoans = createMixedActiveLoans();
            when(loanRepository.findActiveLoans()).thenReturn(allActiveLoans);

            // When: 연체 도서를 조회함
            List<Loan> overdueBooks = libraryService.getOverdueBooks(BASE_DATE);

            // Then: 연체된 도서들만 반환됨
            assertThat(overdueBooks).hasSize(1);
            assertThat(overdueBooks.get(0).isOverdue(BASE_DATE)).isTrue();
        }
    }

    // ===== Test Helper Methods =====

    /**
     * 테스트 데이터 초기화
     */
    private void initializeTestData() {
        // 대출 가능한 도서
        availableBook = new Book("클린 코드", "로버트 마틴", VALID_ISBN);
        
        // 이미 대출된 도서
        borrowedBook = new Book("리팩토링", "마틴 파울러", "978-89-123-4567-9");
        borrowedBook.borrow();
        
        // 정상 회원 (대출 가능)
        normalMember = new Member(VALID_MEMBER_ID, "김개발");
        
        // 대출 한도 초과 회원
        limitExceededMember = new Member("M20250002", "박한도");
        for (int i = 0; i < MEMBER_BORROW_LIMIT; i++) {
            limitExceededMember.borrowBook();
        }
    }

    /**
     * 활성 대출 생성
     */
    private Loan createActiveLoan() {
        Loan loan = new Loan(availableBook, normalMember, BASE_DATE.minusDays(10));
        availableBook.borrow();
        normalMember.borrowBook();
        return loan;
    }

    /**
     * 회원 대출 목록 생성
     */
    private List<Loan> createMemberLoans() {
        Book book1 = new Book("자바의 정석", "남궁성", "978-89-123-1111-1");
        Book book2 = new Book("이펙티브 자바", "조슈아 블로크", "978-89-123-2222-2");
        
        return Arrays.asList(
            new Loan(book1, normalMember, BASE_DATE.minusDays(5)),
            new Loan(book2, normalMember, BASE_DATE.minusDays(3))
        );
    }

    /**
     * 혼재된 활성 대출 목록 생성 (연체된 것과 안 된 것)
     */
    private List<Loan> createMixedActiveLoans() {
        Loan normalLoan = new Loan(availableBook, normalMember, BASE_DATE.minusDays(5));  // 연체 아님
        Loan overdueLoan = new Loan(borrowedBook, normalMember, BASE_DATE.minusDays(20)); // 연체됨
        
        return Arrays.asList(normalLoan, overdueLoan);
    }

    // ===== Given Methods (Mock 설정) =====

    private void givenBookExists(String isbn, Book book) {
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
    }

    private void givenBookNotExists(String isbn) {
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
    }

    private void givenMemberExists(String memberId, Member member) {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    }

    private void givenMemberNotExists(String memberId) {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
    }

    private void givenActiveLoanExists(String isbn, Loan loan) {
        when(loanRepository.findActiveLoanByBookIsbn(isbn)).thenReturn(Optional.of(loan));
    }

    private void givenActiveLoanNotExists(String isbn) {
        when(loanRepository.findActiveLoanByBookIsbn(isbn)).thenReturn(Optional.empty());
    }

    private void givenLoanCanBeSaved() {
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ===== Verification Methods =====

    private void verifyAllRepositoriesSaved() {
        verify(bookRepository).save(any(Book.class));
        verify(memberRepository).save(any(Member.class));
        verify(loanRepository).save(any(Loan.class));
    }
}