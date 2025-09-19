// ===== RED 단계: 실패하는 테스트 작성 =====

// src/test/java/com.example.library/model/BookTest.java
package com.example.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

class BookTest {
    
    @Nested
    @DisplayName("도서 생성 테스트")
    class BookCreationTest {
        
        @Test
        @DisplayName("정상적인 도서 정보로 생성할 수 있다")
        void createBook_WithValidData_ShouldSucceed() {
            // Given
            String title = "클린 코드";
            String author = "로버트 C. 마틴";
            String isbn = "978-89-6626-311-3";
            
            // When
            Book book = new Book(title, author, isbn);
            
            // Then
            assertThat(book.getTitle()).isEqualTo(title);
            assertThat(book.getAuthor()).isEqualTo(author);
            assertThat(book.getIsbn()).isEqualTo(isbn);
            assertThat(book.isAvailable()).isTrue();
        }
        
        @Test
        @DisplayName("제목이 null이면 예외가 발생한다")
        void createBook_WithNullTitle_ShouldThrowException() {
            // Given
            String title = null;
            String author = "로버트 C. 마틴";
            String isbn = "978-89-6626-311-3";
            
            // When & Then
            assertThatThrownBy(() -> new Book(title, author, isbn))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("제목은 필수입니다");
        }
        
        @Test
        @DisplayName("제목이 빈 문자열이면 예외가 발생한다")
        void createBook_WithEmptyTitle_ShouldThrowException() {
            // Given
            String title = "";
            String author = "로버트 C. 마틴";
            String isbn = "978-89-6626-311-3";
            
            // When & Then
            assertThatThrownBy(() -> new Book(title, author, isbn))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("제목은 필수입니다");
        }
        
        @Test
        @DisplayName("저자가 null이면 예외가 발생한다")
        void createBook_WithNullAuthor_ShouldThrowException() {
            // Given
            String title = "클린 코드";
            String author = null;
            String isbn = "978-89-6626-311-3";
            
            // When & Then
            assertThatThrownBy(() -> new Book(title, author, isbn))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("저자는 필수입니다");
        }
        
        @Test
        @DisplayName("ISBN이 null이면 예외가 발생한다")
        void createBook_WithNullIsbn_ShouldThrowException() {
            // Given
            String title = "클린 코드";
            String author = "로버트 C. 마틴";
            String isbn = null;
            
            // When & Then
            assertThatThrownBy(() -> new Book(title, author, isbn))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ISBN은 필수입니다");
        }
    }
    
    @Nested
    @DisplayName("도서 대출 테스트")
    class BookBorrowTest {
        
        @Test
        @DisplayName("대출 가능한 도서를 대출할 수 있다")
        void borrowBook_WhenAvailable_ShouldSucceed() {
            // Given
            Book book = new Book("테스트 도서", "테스트 저자", "TEST-ISBN");
            
            // When
            book.borrow();
            
            // Then
            assertThat(book.isAvailable()).isFalse();
        }
        
        @Test
        @DisplayName("이미 대출된 도서를 다시 대출하려 하면 예외가 발생한다")
        void borrowBook_WhenAlreadyBorrowed_ShouldThrowException() {
            // Given
            Book book = new Book("테스트 도서", "테스트 저자", "TEST-ISBN");
            book.borrow(); // 이미 대출됨
            
            // When & Then
            assertThatThrownBy(book::borrow)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 대출된 도서입니다");
        }
    }
    
    @Nested
    @DisplayName("도서 반납 테스트")
    class BookReturnTest {
        
        @Test
        @DisplayName("대출된 도서를 반납할 수 있다")
        void returnBook_WhenBorrowed_ShouldSucceed() {
            // Given
            Book book = new Book("테스트 도서", "테스트 저자", "TEST-ISBN");
            book.borrow(); // 먼저 대출
            
            // When
            book.returnBook();
            
            // Then
            assertThat(book.isAvailable()).isTrue();
        }
        
        @Test
        @DisplayName("대출되지 않은 도서를 반납하려 하면 예외가 발생한다")
        void returnBook_WhenNotBorrowed_ShouldThrowException() {
            // Given
            Book book = new Book("테스트 도서", "테스트 저자", "TEST-ISBN");
            
            // When & Then
            assertThatThrownBy(book::returnBook)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("대출되지 않은 도서입니다");
        }
    }
}