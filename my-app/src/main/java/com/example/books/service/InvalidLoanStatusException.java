package com.example.books.service;

public class InvalidLoanStatusException extends RuntimeException {
    public InvalidLoanStatusException(String message) {
        super(message);
    }
}