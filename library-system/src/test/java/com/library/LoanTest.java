package com.library;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private Member mockMember;

    @Mock
    private Book mockBook;

    @InjectMocks
    private Loan loan = new Loan(mockMember, mockBook, LocalDate.of(2025, 9, 1));

    @Test
    void testProcessBorrow_success() {
        // given
        when(mockMember.canBorrow()).thenReturn(true);

        // when
        loan.processBorrow();

        // then
        verify(mockMember, times(1)).borrowBook();
        verify(mockBook, times(1)).borrow();
    }

    @Test
    void testProcessBorrow_limitExceeded() {
        // given
        when(mockMember.canBorrow()).thenReturn(false);

        // when & then
        assertThrows(IllegalStateException.class, loan::processBorrow);

        verify(mockMember, never()).borrowBook();
        verify(mockBook, never()).borrow();
    }

    @Test
    void testProcessReturn_success() {
        // when
        loan.processReturn(LocalDate.of(2025, 9, 10));

        // then
        verify(mockMember, times(1)).returnBook();
        verify(mockBook, times(1)).returnBook();
    }

    @Test
    void testDueDateCalculation() {
        assertEquals(LocalDate.of(2025, 9, 15), loan.getDueDate());
    }
}
