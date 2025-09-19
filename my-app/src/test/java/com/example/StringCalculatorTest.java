package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringCalculatorTest {

    @Test 
    @DisplayName("빈 문자열은 0")
    void testEmptyReturnsZero() {
      int result = new StringCalculator().add("");
      System.out.println("빈 문자열 결과: " + result);
      assertEquals(0, result);
    }

    @Test 
    @DisplayName("\"1,2\" -> 3")
    void testTwoNumbers() {
      int result = new StringCalculator().add("1,2");
      System.out.println("\"1,2\" 결과: " + result);
      assertEquals(3, result);
    }

    @Test 
    @DisplayName("\"1\\n2,3\" -> 6 (개행 허용)")
    void testNewLineAsDelimiter() {
      int result = new StringCalculator().add("1\n2,3");
      System.out.println("\"1\\n2,3\" 결과: " + result);
      assertEquals(6, result);
    }

    @Test 
    @DisplayName("커스텀 구분자 ; 지원")
    void testCustomDelimiter() {
      int result = new StringCalculator().add("//;\n1;2");
      System.out.println("\"//;\\n1;2\" 결과: " + result);
      assertEquals(3, result);
    }

    @Test 
    @DisplayName("음수는 예외")
    void testNegativeNumbers() {
      IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
              () -> new StringCalculator().add("1,-2,3,-5"));
      System.out.println("음수 예외 메시지: " + ex.getMessage());
      assertTrue(ex.getMessage().contains("-2"));
      assertTrue(ex.getMessage().contains("-5"));
    }

}
