package com.example.books.service;

import com.example.books.Book;
import com.example.books.User;
import com.example.books.Loan;
import com.example.books.LoanStatus;
import com.example.books.repository.BookRepository;
import com.example.books.repository.UserRepository;
import com.example.books.repository.LoanRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService 테스트")
class LoanServiceTest {
    
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private NotificationService notificationService;
    
    private LoanService loanService;
    
    @BeforeEach
    void setUp() {
        loanService = new LoanService(bookRepository, userRepository, 
                                    loanRepository, notificationService, 14);
    }
    
    @Test
    @DisplayName("유효한 사용자와 도서로 대출이 가능하다")
    void loanBookSuccessfully() {
        // given
        String userId = "user123";
        String isbn = "978-1234567890";
        
        User user = createUser(userId);
        Book book = createBook(isbn);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.isBookAvailable(isbn)).thenReturn(true);
        when(loanRepository.countActiveLoansByUserId(userId)).thenReturn(0);
        
        // when
        Loan loan = loanService.loanBook(userId, isbn);
        
        // then
        assertThat(loan.getUserId()).isEqualTo(userId);
        assertThat(loan.getIsbn()).isEqualTo(isbn);
        assertThat(loan.getLoanDate()).isEqualTo(LocalDate.now());
        assertThat(loan.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        
        verify(loanRepository).save(loan);
        verify(notificationService).sendLoanConfirmation(user, book);
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자의 대출 요청 시 예외가 발생한다")
    void loanBookWithNonExistentUser() {
        // given
        String userId = "nonexistent";
        String isbn = "978-1234567890";
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> loanService.loanBook(userId, isbn))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("사용자를 찾을 수 없습니다: " + userId);
    }
    
    @Test
    @DisplayName("존재하지 않는 도서의 대출 요청 시 예외가 발생한다")
    void loanBookWithNonExistentBook() {
        // given
        String userId = "user123";
        String isbn = "978-0000000000";
        
        User user = createUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> loanService.loanBook(userId, isbn))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("도서를 찾을 수 없습니다: " + isbn);
    }
    
    @Test
    @DisplayName("이미 대출 중인 도서 대출 시 예외가 발생한다")
    void loanUnavailableBook() {
        // given
        String userId = "user123";
        String isbn = "978-1234567890";
        
        User user = createUser(userId);
        Book book = createBook(isbn);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.isBookAvailable(isbn)).thenReturn(false);
        
        // when & then
        assertThatThrownBy(() -> loanService.loanBook(userId, isbn))
            .isInstanceOf(BookNotAvailableException.class)
            .hasMessage("현재 대출 중인 도서입니다: " + isbn);
    }
    
    @Test
    @DisplayName("대출 한도 초과 시 예외가 발생한다")
    void loanBookExceedsLimit() {
        // given
        String userId = "user123";
        String isbn = "978-1234567890";
        
        User user = createUser(userId);
        Book book = createBook(isbn);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.isBookAvailable(isbn)).thenReturn(true);
        when(loanRepository.countActiveLoansByUserId(userId)).thenReturn(5); // 한도 달성
        
        // when & then
        assertThatThrownBy(() -> loanService.loanBook(userId, isbn))
            .isInstanceOf(LoanLimitExceededException.class)
            .hasMessage("대출 한도를 초과했습니다. 현재: 5/5");
    }
    
    @Test
    @DisplayName("도서를 정상적으로 반납할 수 있다")
    void returnBookSuccessfully() {
        // given
        String loanId = "loan123";
        String userId = "user123";
        String isbn = "978-1234567890";
        
        Loan loan = new Loan(loanId, userId, isbn, LocalDate.now().minusDays(7), 14);
        User user = createUser(userId);
        Book book = createBook(isbn);
        
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        
        // when
        loanService.returnBook(loanId);
        
        // then
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(loan.getReturnDate()).isEqualTo(LocalDate.now());
        
        verify(loanRepository).save(loan);
        verify(notificationService).sendReturnConfirmation(user, book);
    }
    
    @Test
    @DisplayName("존재하지 않는 대출 정보로 반납 시 예외가 발생한다")
    void returnNonExistentLoan() {
        // given
        String loanId = "nonexistent";
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> loanService.returnBook(loanId))
            .isInstanceOf(LoanNotFoundException.class)
            .hasMessage("대출 정보를 찾을 수 없습니다: " + loanId);
    }
    
    @Test
    @DisplayName("이미 반납된 도서 반납 시 예외가 발생한다")
    void returnAlreadyReturnedBook() {
        // given
        String loanId = "loan123";
        String userId = "user123";
        String isbn = "978-1234567890";
        
        Loan loan = new Loan(loanId, userId, isbn, LocalDate.now().minusDays(7), 14);
        loan.returnBook(LocalDate.now().minusDays(1)); // 이미 반납됨
        
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        
        // when & then
        assertThatThrownBy(() -> loanService.returnBook(loanId))
            .isInstanceOf(InvalidLoanStatusException.class)
            .hasMessage("이미 반납된 도서입니다");
    }
    
    @Test
    @DisplayName("연체된 대출 목록을 조회할 수 있다")
    void getOverdueLoans() {
        // given
        List<Loan> overdueLoans = Arrays.asList(
            new Loan("loan1", "user1", "isbn1", LocalDate.now().minusDays(20), 14),
            new Loan("loan2", "user2", "isbn2", LocalDate.now().minusDays(25), 14)
        );
        
        when(loanRepository.findOverdueLoans(LocalDate.now())).thenReturn(overdueLoans);
        
        // when
        List<Loan> result = loanService.getOverdueLoans();
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(overdueLoans);
    }
    
    private User createUser(String userId) {
        return new User(userId, "테스트사용자", "test@example.com", 5);
    }
    
    private Book createBook(String isbn) {
        return new Book(isbn, "테스트도서", "테스트저자");
    }
}