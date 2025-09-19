
// ===== RED 단계: 실패하는 테스트 작성 =====

// src/test/java/com/library/model/MemberTest.java
package com.example.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

class MemberTest {
    
    @Nested
    @DisplayName("회원 생성 테스트")
    class MemberCreationTest {
        
        @Test
        @DisplayName("정상적인 회원 정보로 생성할 수 있다")
        void createMember_WithValidData_ShouldSucceed() {
            // Given
            String memberId = "M001";
            String name = "김개발";
            
            // When
            Member member = new Member(memberId, name);
            
            // Then
            assertThat(member.getMemberId()).isEqualTo(memberId);
            assertThat(member.getName()).isEqualTo(name);
            assertThat(member.getBorrowedBooksCount()).isZero();
            assertThat(member.canBorrow()).isTrue();
        }
        
        @Test
        @DisplayName("회원ID가 null이면 예외가 발생한다")
        void createMember_WithNullMemberId_ShouldThrowException() {
            // Given
            String memberId = null;
            String name = "김개발";
            
            // When & Then
            assertThatThrownBy(() -> new Member(memberId, name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("회원ID는 필수입니다");
        }
        
        @Test
        @DisplayName("이름이 null이면 예외가 발생한다")
        void createMember_WithNullName_ShouldThrowException() {
            // Given
            String memberId = "M001";
            String name = null;
            
            // When & Then
            assertThatThrownBy(() -> new Member(memberId, name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 필수입니다");
        }
    }
    
    @Nested
    @DisplayName("대출 한도 테스트")
    class BorrowLimitTest {
        
        @Test
        @DisplayName("3권 미만 대출 시 추가 대출이 가능하다")
        void canBorrow_WithLessThanThreeBooks_ShouldReturnTrue() {
            // Given
            Member member = new Member("M001", "김개발");
            member.borrowBook(); // 1권 대출
            member.borrowBook(); // 2권 대출
            
            // When & Then
            assertThat(member.canBorrow()).isTrue();
            assertThat(member.getBorrowedBooksCount()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("3권 대출 시 추가 대출이 불가능하다")
        void canBorrow_WithThreeBooks_ShouldReturnFalse() {
            // Given
            Member member = new Member("M001", "김개발");
            member.borrowBook(); // 1권 대출
            member.borrowBook(); // 2권 대출
            member.borrowBook(); // 3권 대출
            
            // When & Then
            assertThat(member.canBorrow()).isFalse();
            assertThat(member.getBorrowedBooksCount()).isEqualTo(3);
        }
        
        @Test
        @DisplayName("대출 한도 초과 시 예외가 발생한다")
        void borrowBook_ExceedingLimit_ShouldThrowException() {
            // Given
            Member member = new Member("M001", "김개발");
            member.borrowBook(); // 1권
            member.borrowBook(); // 2권
            member.borrowBook(); // 3권 (한도 달성)
            
            // When & Then
            assertThatThrownBy(member::borrowBook)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("대출 한도를 초과했습니다 (최대 3권)");
        }
        
        @Test
        @DisplayName("도서 반납 시 대출 가능 상태가 된다")
        void returnBook_WhenAtLimit_ShouldAllowBorrowing() {
            // Given
            Member member = new Member("M001", "김개발");
            member.borrowBook(); // 1권
            member.borrowBook(); // 2권
            member.borrowBook(); // 3권 (한도 달성)
            
            // When
            member.returnBook();
            
            // Then
            assertThat(member.canBorrow()).isTrue();
            assertThat(member.getBorrowedBooksCount()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("대출한 책이 없는데 반납하려 하면 예외가 발생한다")
        void returnBook_WithNoBorrowedBooks_ShouldThrowException() {
            // Given
            Member member = new Member("M001", "김개발");
            
            // When & Then
            assertThatThrownBy(member::returnBook)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("반납할 도서가 없습니다");
        }
    }
}