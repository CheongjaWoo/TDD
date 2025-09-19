// EmailSender.java
package com.example.user;

public interface EmailSender {
    void sendWelcomeEmail(User user);
    void send(String to, String subject, String body);
}
