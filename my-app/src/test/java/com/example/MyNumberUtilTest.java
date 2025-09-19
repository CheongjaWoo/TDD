package com.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyNumberUtilTest {
  @ParameterizedTest
  @CsvSource({
          "2, true",
          "3, false",
          "7, false"
  })
  void testParam(int value, boolean expected) {

     boolean actual = MyNumberUtil.isEven(value);
    // 콘솔 출력 (True / False 결과 확인)
    System.out.printf("입력값: %d → 기대값: %b, 실제값: %b%n", value, expected, actual);

    // given: CSV에서 value, expected 제공
    // when & then
    assertEquals(expected, actual);
  }
}
