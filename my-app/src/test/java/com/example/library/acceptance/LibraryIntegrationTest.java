// ===== 통합 테스트 및 인수 테스트 =====

// src/test/java/com/library/acceptance/LibraryIntegrationTest.java
package com.example.library.acceptance;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Loan;
import com.example.library.service.LibraryService;
import com.example.library.repository.*;
import com.example.library.exception.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 도서관 시스템 통합 테스트 클래스
 * ================================
 * 
 * 실제 사용자 시나리오를 기반으로 한 End-to-End 테스트
 * - 실제 Repository 구현체 사용 (InMemory)
 * - 전체 비즈니스 플로우 검증
 * - 사용자 관점에서의 기능 검증
 * - 복잡한 시나리오와 예외 상황 테스트
 */
@DisplayName("📚 도서관 시스템 통합 테스트")
class LibraryIntegrationTest {
    
    // ===== 통합 테스트를 위한 실제 구현체들 =====
    private LibraryService libraryService;          // 테스트 대상 서비스
    private InMemoryBookRepository bookRepository;   // 실제 Repository 구현체
    private InMemoryMemberRepository memberRepository;
    private InMemoryLoanRepository loanRepository;
    
    /**
     * 각 테스트 실행 전 초기화
     * - 실제 Repository 구현체들 생성
     * - LibraryService 의존성 주입
     * - 테스트용 기본 데이터 설정
     */
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // 💡 DisplayName 출력
        System.out.println("\n🚀 " + testInfo.getDisplayName());
        
        // Repository 구현체들 초기화 (실제 메모리 저장소)
        bookRepository = new InMemoryBookRepository();
        memberRepository = new InMemoryMemberRepository();
        loanRepository = new InMemoryLoanRepository();
        
        // 실제 서비스 객체 생성 (의존성 주입)
        libraryService = new LibraryService(bookRepository, memberRepository, loanRepository);
        
        // 테스트를 위한 기본 데이터 준비
        setupTestData();

        // DisplayName 콘솔 출력
        System.out.println("\n▶ [테스트 시작]: " + testInfo.getDisplayName());
    }
    
    /**
     * 테스트용 기본 데이터 설정
     * - 다양한 장르의 도서 4권 등록
     * - 서로 다른 특성의 회원 3명 등록
     * - 실제 시나리오에서 사용할 수 있는 현실적인 데이터
     */
    private void setupTestData() {
        // ===== 도서 데이터 준비 =====
        // 실제 존재하는 기술서적들로 현실감 있는 테스트 데이터 구성
        Book book1 = new Book("클린 코드", "로버트 C. 마틴", "978-89-6626-311-3");
        Book book2 = new Book("리팩터링", "마틴 파울러", "978-89-6626-312-4");
        Book book3 = new Book("이펙티브 자바", "조슈아 블로크", "978-89-6626-313-5");
        Book book4 = new Book("디자인 패턴", "GoF", "978-89-6626-314-6");
        
        // Repository에 도서 저장
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        
        // ===== 회원 데이터 준비 =====
        // 다양한 대출 패턴을 테스트하기 위한 회원들
        Member member1 = new Member("M001", "김개발");    // 일반적인 개발자
        Member member2 = new Member("M002", "박테스트");  // 테스트 전문가
        Member member3 = new Member("M003", "이자바");    // 자바 개발자
        
        // Repository에 회원 저장
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
    }
    
    /**
     * 실제 사용자 관점의 시나리오 테스트
     * 도서관을 이용하는 일반적인 사용 패턴들을 검증
     */
    @Nested
    @DisplayName("👤 사용자 시나리오 테스트")
    class UserScenarioTest {
        
        /**
         * 가장 기본적인 도서관 이용 시나리오
         * 대출 신청 → 도서 이용 → 정상 반납의 전체 과정 검증
         */
        @Test
        @DisplayName("✅ 시나리오: 도서 대출부터 반납까지 전체 과정")
        void fullBorrowAndReturnScenario() {
            // ===== Given: 시나리오 준비 =====
            // 김개발 회원이 클린 코드 도서를 대출하려고 하는 상황
            String isbn = "978-89-6626-311-3";        // 클린 코드 ISBN
            String memberId = "M001";                 // 김개발 회원 ID
            LocalDate loanDate = LocalDate.now();     // 오늘 대출
            
            // ===== When: 도서 대출 실행 =====
            Loan loan = libraryService.borrowBook(isbn, memberId, loanDate);
            
            // ===== Then: 대출 결과 검증 =====
            // 대출 정보가 올바르게 생성되었는지 확인
            assertThat(loan).isNotNull();
            assertThat(loan.getBook().getTitle()).isEqualTo("클린 코드");
            assertThat(loan.getMember().getName()).isEqualTo("김개발");
            assertThat(loan.getLoanDate()).isEqualTo(loanDate);
            assertThat(loan.getDueDate()).isEqualTo(loanDate.plusDays(14)); // 대출 기간 14일
            
            // 도서 상태 변경 확인: 대출 가능 → 대출중
            Book book = bookRepository.findByIsbn(isbn).get();
            assertThat(book.isAvailable()).isFalse();
            
            // 회원 상태 변경 확인: 대출 수량 증가
            Member member = memberRepository.findById(memberId).get();
            assertThat(member.getBorrowedBooksCount()).isEqualTo(1);
            
            // ===== When: 도서 반납 실행 (연체 없는 정상 반납) =====
            LocalDate returnDate = loanDate.plusDays(7); // 7일 후 반납 (14일 내)
            Loan returnedLoan = libraryService.returnBook(isbn, returnDate);
            
            // ===== Then: 반납 결과 검증 =====
            // 반납 정보가 올바르게 처리되었는지 확인
            assertThat(returnedLoan.isReturned()).isTrue();
            assertThat(returnedLoan.getReturnDate()).isEqualTo(returnDate);
            
            // 도서 상태 복구 확인: 대출중 → 대출 가능
            assertThat(book.isAvailable()).isTrue();
            
            // 회원 상태 복구 확인: 대출 수량 감소
            assertThat(member.getBorrowedBooksCount()).isZero();
            
            // 연체료 확인: 정상 반납이므로 연체료 없음
            assertThat(returnedLoan.calculateLateFee(returnDate)).isZero();
        }
        
        /**
         * 대출 한도 관리 시나리오
         * 회원이 최대 대출 가능 수량까지 대출하고 추가 대출을 시도하는 상황
         */
        @Test
        @DisplayName("📚 시나리오: 한 회원이 여러 권 대출하는 과정")
        void multipleBooksScenario() {
            // ===== Given: 다중 대출 시나리오 준비 =====
            String memberId = "M002";                 // 박테스트 회원
            LocalDate loanDate = LocalDate.now();
            
            // 대출할 도서들의 ISBN (3권 = 대출 한도)
            String[] isbns = {
                "978-89-6626-311-3", // 클린 코드
                "978-89-6626-312-4", // 리팩터링
                "978-89-6626-313-5"  // 이펙티브 자바
            };
            
            // ===== When: 대출 한도까지 순차적으로 대출 =====
            for (String isbn : isbns) {
                libraryService.borrowBook(isbn, memberId, loanDate);
            }
            
            // ===== Then: 대출 한도 도달 상태 검증 =====
            Member member = memberRepository.findById(memberId).get();
            assertThat(member.getBorrowedBooksCount()).isEqualTo(3);  // 최대 3권
            assertThat(member.canBorrow()).isFalse();                // 추가 대출 불가
            
            // 한도 초과 시 예외 발생 확인
            String fourthIsbn = "978-89-6626-314-6"; // 4번째 도서 (디자인 패턴)
            assertThatThrownBy(() -> libraryService.borrowBook(fourthIsbn, memberId, loanDate))
                    .isInstanceOf(MemberBorrowLimitExceededException.class);
            
            // ===== When: 한 권 반납으로 대출 가능 상태 복구 =====
            libraryService.returnBook(isbns[0], loanDate.plusDays(5)); // 클린 코드 반납
            
            // ===== Then: 추가 대출 가능 상태 검증 =====
            assertThat(member.canBorrow()).isTrue();                 // 대출 가능
            assertThat(member.getBorrowedBooksCount()).isEqualTo(2); // 2권으로 감소
            
            // 새로운 도서 대출 가능 확인
            assertThatCode(() -> libraryService.borrowBook(fourthIsbn, memberId, loanDate))
                    .doesNotThrowAnyException();
        }
        
        /**
         * 연체 관리 시나리오
         * 도서를 연체한 상황에서의 연체료 계산과 반납 처리
         */
        @Test
        @DisplayName("⏰ 시나리오: 연체 상황과 연체료 계산")
        void overdueScenario() {
            // ===== Given: 연체 상황 설정 =====
            String isbn = "978-89-6626-313-5";       // 이펙티브 자바
            String memberId = "M003";                // 이자바 회원
            LocalDate loanDate = LocalDate.now().minusDays(20); // 20일 전 대출
            
            // 20일 전에 대출 실행 (현재 기준 6일 연체 상태)
            libraryService.borrowBook(isbn, memberId, loanDate);
            
            // ===== When: 연체 도서 조회 =====
            LocalDate checkDate = LocalDate.now();
            List<Loan> overdueBooks = libraryService.getOverdueBooks(checkDate);
            
            // ===== Then: 연체 도서 확인 =====
            assertThat(overdueBooks).hasSize(1);                    // 연체 도서 1권
            Loan overdueLoan = overdueBooks.get(0);
            assertThat(overdueLoan.getBook().getIsbn()).isEqualTo(isbn);
            assertThat(overdueLoan.isOverdue(checkDate)).isTrue();   // 연체 상태 확인
            
            // ===== When: 연체료 계산 =====
            // 연체 계산: 20일 대출 - 14일 대출기간 = 6일 연체
            int expectedLateFee = 6 * 100; // 6일 × 일당 100원 = 600원
            int actualLateFee = libraryService.calculateLateFee(isbn, checkDate);
            
            // ===== Then: 연체료 검증 =====
            assertThat(actualLateFee).isEqualTo(expectedLateFee);
            
            // ===== When: 연체 상태로 반납 =====
            Loan returnedLoan = libraryService.returnBook(isbn, checkDate);
            
            // ===== Then: 연체 반납 처리 검증 =====
            assertThat(returnedLoan.isReturned()).isTrue();                        // 반납 완료
            assertThat(returnedLoan.calculateLateFee(checkDate)).isEqualTo(expectedLateFee); // 연체료 확정
        }
        
        /**
         * 도서 순환 대출 시나리오
         * 인기 도서를 여러 회원이 순차적으로 대출하는 실제 상황
         */
        @Test
        @DisplayName("🔄 시나리오: 같은 도서를 여러 회원이 순차적으로 대출")
        void sequentialBorrowScenario() {
            // ===== Given: 인기 도서 시나리오 준비 =====
            String isbn = "978-89-6626-311-3";       // 인기 도서 "클린 코드"
            LocalDate day1 = LocalDate.now();        // 첫 번째 대출일
            
            // ===== When: 첫 번째 회원이 대출 =====
            libraryService.borrowBook(isbn, "M001", day1); // 김개발이 대출
            
            // ===== Then: 동시 대출 불가 확인 =====
            // 이미 대출된 도서는 다른 회원이 대출할 수 없음
            assertThatThrownBy(() -> libraryService.borrowBook(isbn, "M002", day1))
                    .isInstanceOf(BookNotAvailableException.class);
            
            // ===== When: 첫 번째 회원이 반납 =====
            LocalDate day7 = day1.plusDays(7);
            libraryService.returnBook(isbn, day7);
            
            // ===== Then: 순차 대출 가능 확인 =====
            // 반납 후에는 다른 회원이 대출 가능
            assertThatCode(() -> libraryService.borrowBook(isbn, "M002", day7))
                    .doesNotThrowAnyException();
            
            // 도서 상태가 다시 대출중으로 변경됨
            Book book = bookRepository.findByIsbn(isbn).get();
            assertThat(book.isAvailable()).isFalse();
        }
    }
    
    /**
     * 대출 이력 관리 기능 테스트
     * 회원의 대출 내역 추적과 이력 관리 기능 검증
     */
    @Nested
    @DisplayName("📋 대출 이력 조회 테스트")
    class LoanHistoryTest {
        
        /**
         * 회원의 전체 대출 이력 추적 시나리오
         * 반납 완료된 도서와 현재 대출중인 도서를 포함한 전체 이력 관리
         */
        @Test
        @DisplayName("📖 회원별 대출 이력을 정확히 조회할 수 있다")
        void getMemberLoanHistory() {
            // ===== Given: 복합적인 대출 이력 생성 =====
            String memberId = "M001";                            // 김개발 회원
            LocalDate baseDate = LocalDate.now().minusDays(30);  // 30일 전부터 시작
            
            // 시나리오 1: 첫 번째 대출 → 정상 반납 (완료된 대출)
            libraryService.borrowBook("978-89-6626-311-3", memberId, baseDate);
            libraryService.returnBook("978-89-6626-311-3", baseDate.plusDays(7));
            
            // 시나리오 2: 두 번째 대출 (현재 대출중)
            libraryService.borrowBook("978-89-6626-312-4", memberId, baseDate.plusDays(10));
            
            // 시나리오 3: 세 번째 대출 (현재 대출중)
            libraryService.borrowBook("978-89-6626-313-5", memberId, baseDate.plusDays(15));
            
            // ===== When: 회원의 전체 대출 이력 조회 =====
            List<Loan> loanHistory = libraryService.getMemberLoans(memberId);
            
            // ===== Then: 대출 이력 검증 =====
            assertThat(loanHistory).hasSize(3); // 총 3건의 대출 이력
            
            // 반납 완료 건수 확인
            long returnedCount = loanHistory.stream()
                    .mapToLong(loan -> loan.isReturned() ? 1 : 0)
                    .sum();
            assertThat(returnedCount).isEqualTo(1); // 1건 반납 완료
            
            // 현재 대출중 건수 확인
            long activeLoanCount = loanHistory.stream()
                    .mapToLong(loan -> loan.isReturned() ? 0 : 1)
                    .sum();
            assertThat(activeLoanCount).isEqualTo(2); // 2건 대출중
        }
    }
    
    /**
     * 예외 상황 및 에러 처리 테스트
     * 시스템의 견고성과 예외 상황 대응 능력 검증
     */
    @Nested
    @DisplayName("⚠️ 예외 상황 처리 테스트")
    class ExceptionHandlingTest {
        
        /**
         * 기본적인 데이터 검증 및 예외 처리
         * 존재하지 않는 엔티티에 대한 적절한 예외 발생 확인
         */
        @Test
        @DisplayName("🚫 존재하지 않는 도서나 회원에 대한 적절한 예외 처리")
        void handleNonExistentEntities() {
            LocalDate today = LocalDate.now();
            
            // ===== 존재하지 않는 도서 관련 예외 =====
            // 잘못된 ISBN으로 대출 시도
            assertThatThrownBy(() -> libraryService.borrowBook("INVALID-ISBN", "M001", today))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("도서를 찾을 수 없습니다");
            
            // ===== 존재하지 않는 회원 관련 예외 =====
            // 잘못된 회원 ID로 대출 시도
            assertThatThrownBy(() -> libraryService.borrowBook("978-89-6626-311-3", "INVALID-MEMBER", today))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("회원을 찾을 수 없습니다");
            
            // 잘못된 회원 ID로 대출 이력 조회
            assertThatThrownBy(() -> libraryService.getMemberLoans("INVALID-MEMBER"))
                    .isInstanceOf(MemberNotFoundException.class);
            
            // ===== 잘못된 반납 시도 예외 =====
            // 대출되지 않은 도서의 반납 시도
            assertThatThrownBy(() -> libraryService.returnBook("978-89-6626-311-3", today))
                    .hasMessageContaining("활성 대출을 찾을 수 없습니다");
        }
        
        /**
         * 복잡한 실제 상황 시뮬레이션
         * 다수의 회원과 도서가 얽힌 복합적인 비즈니스 시나리오 테스트
         */
        @Test
        @DisplayName("🔄 복잡한 비즈니스 시나리오 처리")
        void complexBusinessScenario() {
            // ===== Given: 복합 시나리오 환경 설정 =====
            LocalDate baseDate = LocalDate.now().minusDays(30); // 30일 전부터 시작
            
            // 테스트 참여자들
            String[] memberIds = {"M001", "M002", "M003"};       // 3명의 회원
            String[] isbns = {
                "978-89-6626-311-3", // 클린 코드
                "978-89-6626-312-4", // 리팩터링
                "978-89-6626-313-5", // 이펙티브 자바
                "978-89-6626-314-6"  // 디자인 패턴
            };
            
            // ===== When: 복합적인 대출 패턴 실행 =====
            // 시나리오 진행:
            // 1. M001이 클린 코드 대출 (30일 전)
            libraryService.borrowBook(isbns[0], memberIds[0], baseDate);
            
            // 2. M002가 리팩터링 대출 (28일 전)
            libraryService.borrowBook(isbns[1], memberIds[1], baseDate.plusDays(2));
            
            // 3. M001이 클린 코드 반납 (25일 전) - 정상 반납
            libraryService.returnBook(isbns[0], baseDate.plusDays(5));
            
            // 4. M003이 클린 코드 재대출 (24일 전)
            libraryService.borrowBook(isbns[0], memberIds[2], baseDate.plusDays(6));
            
            // ===== Then: 시나리오 결과 종합 검증 =====
            
            // 현재 대출중인 도서 상태 확인
            List<Loan> activeLoans = loanRepository.findActiveLoans();
            assertThat(activeLoans).hasSize(2); // 리팩터링(M002), 클린 코드(M003)
            
            // 각 회원의 대출 이력 패턴 확인
            assertThat(libraryService.getMemberLoans("M001")).hasSize(1); // 1건 (반납 완료)
            assertThat(libraryService.getMemberLoans("M002")).hasSize(1); // 1건 (대출중)
            assertThat(libraryService.getMemberLoans("M003")).hasSize(1); // 1건 (대출중)
            
            // 연체 상황 분석 (현재 날짜 기준으로 모든 대출이 연체)
            List<Loan> overdueBooks = libraryService.getOverdueBooks(LocalDate.now());
            assertThat(overdueBooks).hasSize(2); // 현재 대출중인 2권 모두 연체
            
            // 연체료 계산 검증
            int lateFeeForRefactoring = libraryService.calculateLateFee(isbns[1], LocalDate.now());
            int lateFeeForCleanCode = libraryService.calculateLateFee(isbns[0], LocalDate.now());
            
            // 두 도서 모두 장기 연체 상태이므로 연체료 발생
            assertThat(lateFeeForRefactoring).isGreaterThan(0); // M002의 리팩터링 연체료
            assertThat(lateFeeForCleanCode).isGreaterThan(0);   // M003의 클린 코드 연체료
            
            // 💡 이 테스트는 실제 도서관에서 발생할 수 있는 복잡한 상황들을
            //    모두 포괄하여 시스템의 견고성을 검증합니다.
        }
    }
}