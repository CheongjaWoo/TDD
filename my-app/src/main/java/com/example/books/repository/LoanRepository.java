package com.example.books.repository;

import com.example.books.Loan;
import com.example.books.LoanStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    void save(Loan loan);
    Optional<Loan> findById(String id);
    List<Loan> findByUserId(String userId);
    List<Loan> findByIsbn(String isbn);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findOverdueLoans(LocalDate currentDate);
    boolean isBookAvailable(String isbn);
    int countActiveLoansByUserId(String userId);
}