// src/test/java/com/example/library/repository/InMemoryMemberRepository.java
package com.example.library.repository;

import com.example.library.model.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMemberRepository implements MemberRepository {
    private final Map<String, Member> members = new ConcurrentHashMap<>();
    
    @Override
    public Member save(Member member) {
        members.put(member.getMemberId(), member);
        return member;
    }
    
    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }
    
    @Override
    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }
    
    public void clear() {
        members.clear();
    }
    
    public int size() {
        return members.size();
    }
}