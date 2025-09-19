package com.example.books.service;

import com.example.books.Book;
import com.example.books.User;
import com.example.books.Loan;

public interface NotificationService {
    void sendLoanConfirmation(User user, Book book);
    void sendReturnConfirmation(User user, Book book);
    void sendOverdueNotification(User user, Book book, long overdueDays);
    void sendDueDateReminder(User user, Book book, int daysUntilDue);
}