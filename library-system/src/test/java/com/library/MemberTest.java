package com.library;

import com.library.model.Member;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberTest {

    // @InjectMocks
    private Member member = new Member("M001", "김개발");

    @Test
    void testBorrowBookWithinLimit() {
        member.borrowBook();
        assertEquals(1, member.getBorrowedBooksCount());
    }

    @Test
    void testBorrowExceedsLimit() {
        member.borrowBook();
        member.borrowBook();
        member.borrowBook();
        assertThrows(IllegalStateException.class, member::borrowBook);
    }

    @Test
    void testReturnBook() {
        member.borrowBook();
        member.returnBook();
        assertEquals(0, member.getBorrowedBooksCount());
    }
}
