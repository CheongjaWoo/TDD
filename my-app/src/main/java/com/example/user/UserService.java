// UserService.java
package com.example.user;
import java.util.Optional;

public class UserService {
    private final UserRepository repo;
    private final EmailSender mail;
    public UserService(UserRepository repo, EmailSender mail){ this.repo=repo; this.mail=mail; }

    public User register(String email, String name){
        User saved = repo.save(new User(null, email, name));
        mail.sendWelcomeEmail(saved);
        return saved;
    }

    public boolean sendNewsletter(Long userId){
        Optional<User> u = repo.findById(userId);
        if(u.isPresent()){
            mail.send(u.get().getEmail(), "News", "Hello " + u.get().getName());
            return true;
        }
        return false;
    }
}
