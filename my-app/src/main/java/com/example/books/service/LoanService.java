package com.example.books.service;

import com.example.books.Book;
import com.example.books.User;
import com.example.books.Loan;
import com.example.books.LoanStatus;
import com.example.books.repository.BookRepository;
import com.example.books.repository.UserRepository;
import com.example.books.repository.LoanRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class LoanService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;
    private final int defaultLoanPeriodDays;
    
    public LoanService(BookRepository bookRepository, 
                      UserRepository userRepository,
                      LoanRepository loanRepository,
                      NotificationService notificationService) {
        this(bookRepository, userRepository, loanRepository, notificationService, 14);
    }
    
    public LoanService(BookRepository bookRepository, 
                      UserRepository userRepository,
                      LoanRepository loanRepository,
                      NotificationService notificationService,
                      int defaultLoanPeriodDays) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
        this.defaultLoanPeriodDays = defaultLoanPeriodDays;
    }
    
    public Loan loanBook(String userId, String isbn) {
        // 사용자 검증
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 도서 검증
        Book book = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다: " + isbn));
        
        // 도서 대출 가능 여부 확인
        if (!loanRepository.isBookAvailable(isbn)) {
            throw new BookNotAvailableException("현재 대출 중인 도서입니다: " + isbn);
        }
        
        // 사용자 대출 한도 확인
        int currentLoanCount = loanRepository.countActiveLoansByUserId(userId);
        if (currentLoanCount >= user.getMaxLoanCount()) {
            throw new LoanLimitExceededException("대출 한도를 초과했습니다. 현재: " + currentLoanCount + "/" + user.getMaxLoanCount());
        }
        
        // 대출 처리
        String loanId = UUID.randomUUID().toString();
        Loan loan = new Loan(loanId, userId, isbn, LocalDate.now(), defaultLoanPeriodDays);
        loanRepository.save(loan);
        
        // 알림 발송
        notificationService.sendLoanConfirmation(user, book);
        
        return loan;
    }
    
    public void returnBook(String loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException("대출 정보를 찾을 수 없습니다: " + loanId));
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new InvalidLoanStatusException("이미 반납된 도서입니다");
        }
        
        loan.returnBook(LocalDate.now());
        loanRepository.save(loan);
        
        // 알림 발송
        User user = userRepository.findById(loan.getUserId()).orElseThrow();
        Book book = bookRepository.findByIsbn(loan.getIsbn()).orElseThrow();
        notificationService.sendReturnConfirmation(user, book);
    }
    
    public List<Loan> getUserLoans(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }
        return loanRepository.findByUserId(userId);
    }
    
    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }
    
    public void sendOverdueNotifications() {
        List<Loan> overdueLoans = getOverdueLoans();
        
        for (Loan loan : overdueLoans) {
            User user = userRepository.findById(loan.getUserId()).orElseThrow();
            Book book = bookRepository.findByIsbn(loan.getIsbn()).orElseThrow();
            long overdueDays = loan.getOverdueDays(LocalDate.now());
            
            notificationService.sendOverdueNotification(user, book, overdueDays);
        }
    }
}