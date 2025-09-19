package com.example.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트
 * JUnit5 + Mockito
 * - @TestMethodOrder 로 테스트 실행 순서 보장
 * - 사용자 등록 & 뉴스레터 발송 시나리오 검증
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private UserRepository userRepository; // DB 접근 역할(Mock 처리)
    private EmailSender emailSender;       // 이메일 발송 역할(Mock 처리)
    private UserService userService;       // 테스트 대상 (System Under Test)

    @BeforeEach
    void setUp() {
        // 매번 fresh한 Mock 객체를 생성
        userRepository = mock(UserRepository.class);
        emailSender = mock(EmailSender.class);

        // UserService 에 Mock 주입
        userService = new UserService(userRepository, emailSender);
    }

    /**
     * 시나리오 1: 사용자 등록 성공
     */
    @Test
    @Order(1) // 실행 순서 1
    @DisplayName("사용자 등록 성공 테스트")
    void testRegisterUser() {
        System.out.println("\n[테스트 시작] 사용자 등록 성공");

        // Given: userRepository.save() 호출 시 mockUser 반환하도록 스텁
        User mockUser = new User(1L, "test@example.com", "Tester");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When: register() 호출
        User saved = userService.register("test@example.com", "Tester");

        // Then: 결과 검증
        assertNotNull(saved);                        // null 아닌지 확인
        assertEquals("Tester", saved.getName());     // 이름 일치 확인
        verify(userRepository).save(any(User.class)); // DB 저장 호출 검증
        verify(emailSender).sendWelcomeEmail(mockUser); // 웰컴 메일 발송 검증

        System.out.println("✅ 테스트 통과: 사용자 등록 성공 + 웰컴 메일 발송");
    }

    /**
     * 시나리오 2: 뉴스레터 전송 성공 (사용자 존재)
     */
    @Test
    @Order(2) // 실행 순서 2
    @DisplayName("뉴스레터 전송 성공 테스트 (사용자 존재)")
    void testSendNewsletter_UserExists() {
        System.out.println("\n[테스트 시작] 뉴스레터 전송 성공");

        // Given: ID=2 사용자가 존재한다고 스텁
        User mockUser = new User(2L, "john@example.com", "John");
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));

        // When: 뉴스레터 발송 실행
        boolean result = userService.sendNewsletter(2L);

        // Then: 결과 검증
        assertTrue(result); // true 반환 확인
        verify(userRepository).findById(2L); // findById 호출 확인
        verify(emailSender).send("john@example.com", "News", "Hello John"); // 메일 발송 호출 검증

        System.out.println("✅ 테스트 통과: 사용자 존재 시 뉴스레터 발송");
    }

    /**
     * 시나리오 3: 뉴스레터 전송 실패 (사용자 없음)
     */
    @Test
    @Order(3) // 실행 순서 3
    @DisplayName("뉴스레터 전송 실패 테스트 (사용자 없음)")
    void testSendNewsletter_UserNotFound() {
        System.out.println("\n[테스트 시작] 뉴스레터 전송 실패 (사용자 없음)");

        // Given: ID=999 사용자가 존재하지 않는다고 스텁
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When: 뉴스레터 발송 시도
        boolean result = userService.sendNewsletter(999L);

        // Then: 결과 검증
        assertFalse(result); // false 반환 확인
        verify(userRepository).findById(999L); // findById 호출 확인
        verify(emailSender, never()).send(anyString(), anyString(), anyString()); // 메일 발송 호출이 없음을 확인

        System.out.println("✅ 테스트 통과: 사용자 없을 때 뉴스레터 전송 안 함");
    }
}
