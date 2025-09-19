// src/test/java/com/example/library/repository/InMemoryLoanRepository.java
package com.example.library.repository;

import com.example.library.model.Loan;
import com.example.library.model.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryLoanRepository implements LoanRepository {
    private final Map<String, Loan> loans = new ConcurrentHashMap<>(); // Long -> String으로 변경
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Loan save(Loan loan) {
        // 대출의 고유 키 생성 (책ISBN + 회원ID + 대출일)
        String loanKey = generateLoanKey(loan);
        
        // 기존 대출인지 확인 (업데이트)
        if (loans.containsKey(loanKey)) {
            loans.put(loanKey, loan);
            return loan;
        }
        
        // 새로운 대출 (추가)
        loans.put(loanKey, loan);
        return loan;
    }
    
    private String generateLoanKey(Loan loan) {
        return loan.getBook().getIsbn() + "_" + 
               loan.getMember().getMemberId() + "_" + 
               loan.getLoanDate().toString();
    }
    
    @Override
    public List<Loan> findByMember(Member member) {
        return loans.values().stream()
                .filter(loan -> loan.getMember().equals(member))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Loan> findActiveLoans() {
        return loans.values().stream()
                .filter(loan -> !loan.isReturned())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Loan> findActiveLoanByBookIsbn(String isbn) {
        return loans.values().stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getBook().getIsbn().equals(isbn))
                .findFirst();
    }
    
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }
    
    public void clear() {
        loans.clear();
        idGenerator.set(1);
    }
    
    public int size() {
        return loans.size();
    }
}