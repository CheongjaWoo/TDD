package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

@DisplayName("Calculator 클래스 테스트")
class CalculatorTest {
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("\n 클래스: " + testInfo.getDisplayName());
    }
    
    @Test
    @DisplayName("두 양수를 더하는 테스트")
    void testAdd_PositiveNumbers() {
        Calculator calculator = new Calculator();
        // Given (준비)
        int a = 5;
        int b = 3;

        // When (실행)
        int result = calculator.add(a, b);
        
        // Then (검증)
        assertEquals(8, result, "5 + 3 = 8 이어야 한다");
    }

}
