package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

  // 최소 길이 체크
  @Test
  void password_should_be_at_least_8_characters_long() {
      PasswordValidator validator = new PasswordValidator();
      assertFalse(validator.validate("short")); // 5자
    //   assertTrue(validator.validate("12345678")); // 8자
  }

  // 대문자 포함 체크
  @Test
  void password_should_contain_at_least_one_uppercase_letter() {
      PasswordValidator validator = new PasswordValidator();
      assertFalse(validator.validate("testpassword"));
    //   assertTrue(validator.validate("Testpassword"));
  }

  // 숫자 포함 체크
  @Test
  void password_should_contain_at_least_one_digit() {
      PasswordValidator validator = new PasswordValidator();
      assertFalse(validator.validate("Password"));
      assertTrue(validator.validate("Password123"));
  }
}
