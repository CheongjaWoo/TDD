package com.example.books;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Book 도메인 테스트")
class BookTest {

    @Test
    @DisplayName("유효한 정보로 도서를 생성할 수 있다")
    void createBookWithValidInfo() {
        // given
        String isbn = "978-1234567890";
        String title = "클린 코드";
        String author = "로버트 마틴";
        
        // when & then
        assertThatNoException().isThrownBy(() -> 
            new Book(isbn, title, author)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123", "invalid-isbn", "12345"})
    @DisplayName("유효하지 않은 ISBN으로 도서 생성 시 예외가 발생한다")
    void createBookWithInvalidIsbn(String invalidIsbn) {
        // when & then
        assertThatThrownBy(() -> 
            new Book(invalidIsbn, "제목", "저자")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("유효하지 않은 ISBN입니다");
    }

    @Test
    @DisplayName("null 값으로 도서 생성 시 예외가 발생한다")
    void createBookWithNullValues() {
        // when & then
        assertThatThrownBy(() -> new Book(null, "제목", "저자"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN은 필수입니다");
            
        assertThatThrownBy(() -> new Book("9781234567890", null, "저자"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("제목은 필수입니다");
            
        assertThatThrownBy(() -> new Book("9781234567890", "제목", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("저자는 필수입니다");
    }

    @Test
    @DisplayName("빈 문자열로 도서 생성 시 예외가 발생한다")
    void createBookWithEmptyValues() {
        // when & then
        assertThatThrownBy(() -> new Book("9781234567890", "", "저자"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("제목은 필수입니다");
    }

    @Test
    @DisplayName("도서 정보를 정확히 반환한다")
    void getBookInfo() {
        // given
        String isbn = "978-1234567890";
        String title = "클린 코드";
        String author = "로버트 마틴";
        Book book = new Book(isbn, title, author);
        
        // when & then
        assertThat(book.getIsbn()).isEqualTo(isbn);
        assertThat(book.getTitle()).isEqualTo(title);
        assertThat(book.getAuthor()).isEqualTo(author);
    }

    @Test
    @DisplayName("동일한 ISBN을 가진 도서는 같은 객체로 판단한다")
    void equalsAndHashCode() {
        // given
        Book book1 = new Book("978-1234567890", "클린 코드", "로버트 마틴");
        Book book2 = new Book("978-1234567890", "다른 제목", "다른 저자");
        Book book3 = new Book("978-0987654321", "클린 코드", "로버트 마틴");
        
        // when & then
        assertThat(book1).isEqualTo(book2);
        assertThat(book1).isNotEqualTo(book3);
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
    }
    
    @Test
    @DisplayName("유효한 ISBN-10 형식을 허용한다")
    void createBookWithValidIsbn10() {
        // given
        String isbn10 = "0123456789";
        
        // when & then
        assertThatNoException().isThrownBy(() -> 
            new Book(isbn10, "제목", "저자")
        );
    }
    
    @Test
    @DisplayName("유효한 ISBN-13 형식을 허용한다")
    void createBookWithValidIsbn13() {
        // given
        String isbn13 = "9781234567890";
        
        // when & then
        assertThatNoException().isThrownBy(() -> 
            new Book(isbn13, "제목", "저자")
        );
    }
    
    @Test
    @DisplayName("하이픈이 포함된 ISBN을 허용한다")
    void createBookWithHyphenatedIsbn() {
        // given
        String isbnWithHyphens = "978-1-234-56789-0";
        
        // when & then
        assertThatNoException().isThrownBy(() -> 
            new Book(isbnWithHyphens, "제목", "저자")
        );
    }
}