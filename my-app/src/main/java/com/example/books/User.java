package com.example.books;

import java.util.Objects;

public class User {
    private final String id;
    private final String name;
    private final String email;
    private final int maxLoanCount;
    
    public User(String id, String name, String email) {
        this(id, name, email, 5); // 기본 대출 한도 5권
    }
    
    public User(String id, String name, String email, int maxLoanCount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }
        if (maxLoanCount <= 0) {
            throw new IllegalArgumentException("대출 한도는 1 이상이어야 합니다");
        }
        
        this.id = id;
        this.name = name;
        this.email = email;
        this.maxLoanCount = maxLoanCount;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getMaxLoanCount() { return maxLoanCount; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}