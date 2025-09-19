package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName(" Calculator 클래스 테스트")
class CalculatorTest {
//    @BeforeEach
//    void setUp(TestInfo testInfo) {
//        System.out.println("▶ [테스트 시작]: " + testInfo.getDisplayName());
//    }

    @Test
    @DisplayName("두 양수를 더하는 테스트")
    void testAdd_PositiveNumbers() {
        Calculator calc = new Calculator();
        assertEquals(5,calc.add(2,3));

    //     Calculator calculator = new Calculator();
    //     // Given (준비)
    //     int a = 5;
    //     int b = 3;

    //     // When (실행)
    //     int result = calculator.add(a, b);

    //     // Then (검증)
    //     assertEquals(8, result, "5 + 3 = 8 이어야 한다");
    }

}
