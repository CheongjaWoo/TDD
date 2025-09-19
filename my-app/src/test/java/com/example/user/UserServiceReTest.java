package com.example.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito Extension 적용
class UserServiceReTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private UserService userService; // Mock 들을 자동으로 주입

    // DisplayName 콘솔 출력
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("\n▶ [테스트 시작]: " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("사용자 등록 성공 테스트")
    void registerUser_success() {
        // Given
        User mockUser = new User(1L, "test@example.com", "Tester");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User saved = userService.register("test@example.com", "Tester");

        // Then
        assertNotNull(saved);
        assertEquals("Tester", saved.getName());
        verify(userRepository).save(any(User.class));
        verify(emailSender).sendWelcomeEmail(mockUser);
    }

    @Test
    @DisplayName("뉴스레터 전송 성공 테스트 (사용자 존재)")
    void sendNewsletter_userExists() {
        // Given
        User mockUser = new User(2L, "john@example.com", "John");
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));

        // When
        boolean result = userService.sendNewsletter(2L);

        // Then
        assertTrue(result);
        verify(userRepository).findById(2L);
        verify(emailSender).send("john@example.com", "News", "Hello John");
    }

    @Test
    @DisplayName("뉴스레터 전송 실패 테스트 (사용자 없음)")
    void sendNewsletter_userNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = userService.sendNewsletter(999L);

        // Then
        assertFalse(result);
        verify(userRepository).findById(999L);
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }
}
