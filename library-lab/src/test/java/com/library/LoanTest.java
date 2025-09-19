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

@ExtendWith(MockitoExtension.class) // MockitoExtension 적용
public class LoanTest {

    @Mock
    private Member mockMember;

    @Mock
    private Book mockBook;

    @InjectMocks
    private Loan loan = new Loan(mockMember, mockBook, LocalDate.of(2025, 9, 1));

    @Test
    void testProcessBorrow_success() {
        // TODO: mockMember.canBorrow() → true 설정
        // loan.processBorrow() 실행
        // verify(mockMember).borrowBook();
        // verify(mockBook).borrow();
    }

    @Test
    void testProcessBorrow_limitExceeded() {
        // TODO: mockMember.canBorrow() → false 설정
        // loan.processBorrow() 실행 시 예외 발생 검증
        // verify(mockMember, never()).borrowBook();
        // verify(mockBook, never()).borrow();
    }

    @Test
    void testProcessReturn_success() {
        // TODO: loan.processReturn(LocalDate.now().plusDays(5));
        // verify(mockMember).returnBook();
        // verify(mockBook).returnBook();
    }

    @Test
    void testDueDateCalculation() {
        // TODO: loan.getDueDate()가 loanDate + 14일인지 검증
    }
}
