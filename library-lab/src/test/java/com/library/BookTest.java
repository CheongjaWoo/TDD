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
        // TODO: book.isAvailable() == true 검증
    }

    @Test
    void testBorrowBook() {
        // TODO: borrow() 호출 후 isAvailable() == false 검증
    }

    @Test
    void testBorrowAlreadyBorrowedBook() {
        // TODO: borrow() 두 번 호출 → 두 번째에서 예외 검증
    }

    @Test
    void testReturnBook() {
        // TODO: borrow() → returnBook() 후 isAvailable() == true 검증
    }

    @Test
    void testReturnBookNotBorrowed() {
        // TODO: 생성 직후 returnBook() 호출 시 예외 검증
    }
}