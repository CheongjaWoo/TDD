package com.library;

import com.library.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookTest {

    @InjectMocks
    private Book book = new Book("클린 코드", "로버트 마틴", "978-89-6626-311-3");

    @Test
    void testNewBookIsAvailable() {
        // given: 새로 생성된 Book 객체
        // then: 처음에는 대출 가능 상태여야 함
        assertTrue(book.isAvailable());
    }

    @Test
    void testBorrowBook() {
        // when: 책을 대출하면
        book.borrow();
        // then: 상태가 대출 불가능(false)로 바뀜
        assertFalse(book.isAvailable());
    }

    @Test
    void testBorrowAlreadyBorrowedBook() {
        // given: 한 번 대출한 후
        book.borrow();
        // then: 다시 대출 시 예외 발생
        assertThrows(IllegalStateException.class, book::borrow);
    }

    @Test
    void testReturnBook() {
        // given: 책을 먼저 대출한 뒤
        book.borrow();
        // when: 반납하면
        book.returnBook();
        // then: 상태가 다시 대출 가능(true)로 바뀜
        assertTrue(book.isAvailable());
    }

    @Test
    void testReturnBookNotBorrowed() {
        // given: 생성 직후 (아직 대출 안 됨)
        // then: 바로 returnBook() 호출 시 예외 발생
        assertThrows(IllegalStateException.class, book::returnBook);
    }
}
