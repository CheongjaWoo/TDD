// src/main/java/com/example/library/service/LibraryService.java
package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Loan;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.exception.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LibraryService 클래스
 * ------------------------------
 * - 도서관의 핵심 비즈니스 로직을 담당하는 서비스 계층
 * - Book, Member, Loan 을 조합하여
 *   도서 대출 / 반납 / 연체 관리 / 연체료 계산 기능 제공
 */
public class LibraryService {
    private final BookRepository bookRepository;     // 도서 저장소
    private final MemberRepository memberRepository; // 회원 저장소
    private final LoanRepository loanRepository;     // 대출 저장소
    
    /**
     * 생성자 주입 (Dependency Injection)
     * - 외부에서 Repository 구현체를 전달받아 사용
     */
    public LibraryService(BookRepository bookRepository, 
                         MemberRepository memberRepository, 
                         LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }
    
    /**
     * 도서 대출 처리
     * 1. ISBN, 회원 ID 로 도서와 회원 조회
     * 2. 대출 가능 여부 검증 (도서/회원)
     * 3. 도서 상태 및 회원 대출 권수 갱신
     * 4. Loan 객체 생성 후 저장
     */
    public Loan borrowBook(String isbn, String memberId, LocalDate loanDate) {
        Book book = findBookByIsbn(isbn);
        Member member = findMemberById(memberId);
        
        validateBookAvailable(book);     // 도서 대출 가능 여부 확인
        validateMemberCanBorrow(member); // 회원 대출 가능 여부 확인
        
        // 도서 대출 처리
        book.borrow();
        member.borrowBook();
        
        // 대출 정보 생성 및 저장
        Loan loan = new Loan(book, member, loanDate);
        
        bookRepository.save(book);
        memberRepository.save(member);
        
        return loanRepository.save(loan);
    }
    
    /**
     * 도서 반납 처리
     * 1. ISBN 으로 활성 Loan 조회
     * 2. Loan, Book, Member 상태 변경
     * 3. 변경된 데이터 저장
     */
    public Loan returnBook(String isbn, LocalDate returnDate) {
        Loan loan = findActiveLoanByIsbn(isbn);
        
        // 반납 처리
        loan.returnBook(returnDate);
        loan.getBook().returnBook();
        loan.getMember().returnBook();
        
        // 저장
        loanRepository.save(loan);
        bookRepository.save(loan.getBook());
        memberRepository.save(loan.getMember());
        
        return loan;
    }
    
    /**
     * 회원의 대출 이력 조회
     */
    public List<Loan> getMemberLoans(String memberId) {
        Member member = findMemberById(memberId);
        return loanRepository.findByMember(member);
    }
    
    /**
     * 연체된 도서 목록 조회
     * - 활성 Loan 중 checkDate 기준으로 연체 여부 필터링
     */
    public List<Loan> getOverdueBooks(LocalDate checkDate) {
        return loanRepository.findActiveLoans()
                .stream()
                .filter(loan -> loan.isOverdue(checkDate))
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 도서의 연체료 계산
     */
    public int calculateLateFee(String isbn, LocalDate checkDate) {
        Loan loan = findActiveLoanByIsbn(isbn);
        return loan.calculateLateFee(checkDate);
    }
    
    // ===== Private Helper Methods =====
    
    /** ISBN 으로 도서 조회 (없으면 BookNotFoundException 발생) */
    private Book findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다: " + isbn));
    }
    
    /** 회원ID 로 회원 조회 (없으면 MemberNotFoundException 발생) */
    private Member findMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
    }
    
    /** ISBN 으로 활성 Loan 조회 (없으면 IllegalStateException 발생) */
    private Loan findActiveLoanByIsbn(String isbn) {
        return loanRepository.findActiveLoanByBookIsbn(isbn)
                .orElseThrow(() -> new IllegalStateException(
                    "해당 도서의 활성 대출을 찾을 수 없습니다: " + isbn));
    }
    
    /** 도서가 대출 가능한 상태인지 검증 */
    private void validateBookAvailable(Book book) {
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("대출 불가능한 도서입니다: " + book.getTitle());
        }
    }
    
    /** 회원이 대출 가능한 상태인지 검증 */
    private void validateMemberCanBorrow(Member member) {
        if (!member.canBorrow()) {
            throw new MemberBorrowLimitExceededException("대출 한도를 초과했습니다: " + member.getName());
        }
    }
}
