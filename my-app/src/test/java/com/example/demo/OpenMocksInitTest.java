package com.example.demo;

import org.junit.jupiter.api.*;
import org.mockito.*;

import com.example.user.User;
import com.example.user.UserRepository;
import com.example.user.UserService;
import com.example.user.EmailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * MockitoAnnotations.openMocks(this) 를 이용한 Mock 초기화 예제
 */
class OpenMocksInitTest {

    // Mock 객체 선언: 실제 구현 대신 Mockito 가 가짜 객체 생성
    @Mock UserRepository repo;
    @Mock EmailSender mail;

    // @InjectMocks → 위에서 만든 repo, mail을 UserService 생성자/필드에 자동 주입
    @InjectMocks UserService service;

    // openMocks() 가 리턴하는 리소스를 닫기 위해 AutoCloseable 보관
    AutoCloseable closeable;

    @BeforeEach
    void init() {
        // 테스트 실행 전에 Mock 필드들을 초기화
        // (이 호출을 안 하면 repo, mail, service 가 null 이 됨)
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        // 테스트 실행 후 Mock 관련 리소스 정리
        closeable.close();
    }

    @Test
    void register_with_openMocks_init() {
        // repo.save() 호출 시 ID 가 7L 인 User 를 반환하도록 Stubbing
        when(repo.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(7L, u.getEmail(), u.getName());
        });

        // 실제 UserService.register() 호출 → 내부적으로 repo.save(), mail.sendWelcomeEmail() 실행
        User saved = service.register("lee@example.com","Lee");

        // 저장된 User 의 id 값이 우리가 Stubbing 한 7L 인지 검증
        assertEquals(7L, saved.getId());
    }
}
