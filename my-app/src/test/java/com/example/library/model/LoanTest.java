// ===== RED 단계: 실패하는 테스트 작성 =====

// src/test/java/com/library/model/LoanTest.java
package com.example.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

class LoanTest {
    
    private Book book;
    private Member member;
    
    @BeforeEach
    void setUp() {
        book = new Book("테스트 도서", "테스트 저자", "TEST-ISBN");
        member = new Member("M001", "김개발");
    }
    
    @Nested
    @DisplayName("대출 생성 테스트")
    class LoanCreationTest {
        
        @Test
        @DisplayName("정상적인 대출 정보로 생성할 수 있다")
        void createLoan_WithValidData_ShouldSucceed() {
            // Given
            LocalDate loanDate = LocalDate.now();
            
            // When
            Loan loan = new Loan(book, member, loanDate);
            
            // Then
            assertThat(loan.getBook()).isEqualTo(book);
            assertThat(loan.getMember()).isEqualTo(member);
            assertThat(loan.getLoanDate()).isEqualTo(loanDate);
            assertThat(loan.getDueDate()).isEqualTo(loanDate.plusDays(14));
            assertThat(loan.getReturnDate()).isNull();
            assertThat(loan.isReturned()).isFalse();
        }
        
        @Test
        @DisplayName("도서가 null이면 예외가 발생한다")
        void createLoan_WithNullBook_ShouldThrowException() {
            // Given
            Book nullBook = null;
            LocalDate loanDate = LocalDate.now();
            
            // When & Then
            assertThatThrownBy(() -> new Loan(nullBook, member, loanDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("도서는 필수입니다");
        }
        
        @Test
        @DisplayName("회원이 null이면 예외가 발생한다")
        void createLoan_WithNullMember_ShouldThrowException() {
            // Given
            Member nullMember = null;
            LocalDate loanDate = LocalDate.now();
            
            // When & Then
            assertThatThrownBy(() -> new Loan(book, nullMember, loanDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("회원은 필수입니다");
        }
        
        @Test
        @DisplayName("대출일이 null이면 예외가 발생한다")
        void createLoan_WithNullLoanDate_ShouldThrowException() {
            // Given
            LocalDate nullLoanDate = null;
            
            // When & Then
            assertThatThrownBy(() -> new Loan(book, member, nullLoanDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("대출일은 필수입니다");
        }
    }
    
    @Nested
    @DisplayName("반납 처리 테스트")
    class ReturnProcessTest {
        
        @Test
        @DisplayName("정상적으로 반납할 수 있다")
        void returnBook_OnTime_ShouldSucceed() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(7);
            LocalDate returnDate = LocalDate.now();
            Loan loan = new Loan(book, member, loanDate);
            
            // When
            loan.returnBook(returnDate);
            
            // Then
            assertThat(loan.isReturned()).isTrue();
            assertThat(loan.getReturnDate()).isEqualTo(returnDate);
            assertThat(loan.isOverdue(returnDate)).isFalse();
            assertThat(loan.calculateLateFee(returnDate)).isZero();
        }
        
        @Test
        @DisplayName("이미 반납된 도서를 다시 반납하려 하면 예외가 발생한다")
        void returnBook_AlreadyReturned_ShouldThrowException() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(7);
            LocalDate returnDate = LocalDate.now();
            Loan loan = new Loan(book, member, loanDate);
            loan.returnBook(returnDate); // 이미 반납
            
            // When & Then
            assertThatThrownBy(() -> loan.returnBook(LocalDate.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 반납된 도서입니다");
        }
        
        @Test
        @DisplayName("대출일 이전 날짜로 반납하려 하면 예외가 발생한다")
        void returnBook_BeforeLoanDate_ShouldThrowException() {
            // Given
            LocalDate loanDate = LocalDate.now();
            LocalDate invalidReturnDate = loanDate.minusDays(1);
            Loan loan = new Loan(book, member, loanDate);
            
            // When & Then
            assertThatThrownBy(() -> loan.returnBook(invalidReturnDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("반납일은 대출일 이후여야 합니다");
        }
    }
    
    @Nested
    @DisplayName("연체 및 연체료 테스트")
    class OverdueAndLateFeeTest {
        
        @Test
        @DisplayName("반납 기한 내 반납 시 연체가 아니다")
        void isOverdue_WithinDueDate_ShouldReturnFalse() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(10);
            LocalDate checkDate = LocalDate.now(); // 14일 이내
            Loan loan = new Loan(book, member, loanDate);
            
            // When & Then
            assertThat(loan.isOverdue(checkDate)).isFalse();
        }
        
        @Test
        @DisplayName("반납 기한 초과 시 연체다")
        void isOverdue_AfterDueDate_ShouldReturnTrue() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(20);
            LocalDate checkDate = LocalDate.now(); // 14일 초과
            Loan loan = new Loan(book, member, loanDate);
            
            // When & Then
            assertThat(loan.isOverdue(checkDate)).isTrue();
        }
        
        @Test
        @DisplayName("기한 내 반납 시 연체료가 없다")
        void calculateLateFee_OnTime_ShouldReturnZero() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(10);
            LocalDate returnDate = LocalDate.now(); // 14일 이내
            Loan loan = new Loan(book, member, loanDate);
            
            // When
            int lateFee = loan.calculateLateFee(returnDate);
            
            // Then
            assertThat(lateFee).isZero();
        }
        
        @Test
        @DisplayName("1일 연체 시 100원의 연체료가 발생한다")
        void calculateLateFee_OneDayLate_ShouldReturn100() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(15);
            LocalDate returnDate = LocalDate.now(); // 1일 연체
            Loan loan = new Loan(book, member, loanDate);
            
            // When
            int lateFee = loan.calculateLateFee(returnDate);
            
            // Then
            assertThat(lateFee).isEqualTo(100);
        }
        
        @Test
        @DisplayName("5일 연체 시 500원의 연체료가 발생한다")
        void calculateLateFee_FiveDaysLate_ShouldReturn500() {
            // Given
            LocalDate loanDate = LocalDate.now().minusDays(19);
            LocalDate returnDate = LocalDate.now(); // 5일 연체
            Loan loan = new Loan(book, member, loanDate);
            
            // When
            int lateFee = loan.calculateLateFee(returnDate);
            
            // Then
            assertThat(lateFee).isEqualTo(500);
        }
    }
}