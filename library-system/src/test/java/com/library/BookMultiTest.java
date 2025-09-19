package com.library;

import com.library.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용 (JUnit5와 연동)
public class BookMultiTest {

    // ✅ @InjectMocks: Mockito가 관리할 고정된 Book 객체
    // 이 객체는 항상 같은 데이터("클린 코드")로 테스트됨
    @InjectMocks
    private Book fixedBook = new Book("클린 코드", "로버트 마틴", "978-89-6626-311-3");

    // --------------------------------------------------
    // [1] @InjectMocks 활용 (고정 객체 테스트)
    // --------------------------------------------------

    @Test
    void testFixedBookBorrowAndReturn() {
        // given: 고정된 Book 객체(fixedBook)는 항상 대출 가능 상태(true)로 시작

        // when: borrow() 실행 → 책을 대출 중 상태(false)로 변경
        fixedBook.borrow();

        // then: isAvailable() == false 검증
        assertFalse(fixedBook.isAvailable());

        // when: returnBook() 실행 → 다시 대출 가능 상태(true)로 변경
        fixedBook.returnBook();

        // then: isAvailable() == true 검증
        assertTrue(fixedBook.isAvailable());
    }

    @Test
    void testFixedBookBorrowTwice_ThrowsException() {
        // given: 고정된 Book 객체 생성 (available == true)

        // when: 첫 번째 borrow() 실행 → 정상 동작
        fixedBook.borrow();

        // then: 두 번째 borrow() 실행 시 예외 발생해야 함
        assertThrows(IllegalStateException.class, fixedBook::borrow);
    }

    // --------------------------------------------------
    // [2] @ParameterizedTest 활용 (여러 데이터 반복 검증)
    // --------------------------------------------------

    @ParameterizedTest
    @CsvSource({
        "클린 코드, 로버트 마틴, 978-1",
        "이펙티브 자바, 조슈아 블로크, 978-2",
        "테스트 주도 개발, 켄트 벡, 978-3"
    })
    void testBookCreationWithDifferentInputs(String title, String author, String isbn) {
        // given: 서로 다른 입력값(title, author, isbn)으로 Book 생성
        Book book = new Book(title, author, isbn);

        // then: 객체가 정상 생성되고, 처음 상태는 항상 대출 가능이어야 함
        assertNotNull(book);
        assertTrue(book.isAvailable());
    }

    @ParameterizedTest
    @CsvSource({
        "클린 코드, 로버트 마틴, 978-1",
        "클린 아키텍처, 로버트 마틴, 978-2"
    })
    void testBorrowAndReturnForDifferentBooks(String title, String author, String isbn) {
        // given: 서로 다른 입력값으로 Book 생성
        Book book = new Book(title, author, isbn);

        // when: borrow() 실행 → 책이 대출 불가능 상태(false)가 됨
        book.borrow();
        assertFalse(book.isAvailable());

        // when: returnBook() 실행 → 책이 다시 대출 가능 상태(true)가 됨
        book.returnBook();
        assertTrue(book.isAvailable());
    }
}
