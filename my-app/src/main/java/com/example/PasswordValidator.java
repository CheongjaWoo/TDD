package com.example;

// 1단계 : 최소 길이 체크
// public class PasswordValidator {
//     public boolean validate(String password) {
//         return password.length() >= 8;
//     }
// }

// // 2단계: 대문자 포함 체크
// public class PasswordValidator {
//   public boolean validate(String password) {
//     // 최소 길이 체크
//     if (password.length() < 8) {
//       return false;
//     }

//     // 대문자 포함 체크
//     boolean hasUppercase = false;
//     for (char c : password.toCharArray()) {
//       if (Character.isUpperCase(c)) {
//         hasUppercase = true;
//         break;
//       }
//     }
//     return hasUppercase;
//   }
// }


// 2단계 리펙토링수정
// public class PasswordValidator {
//   public boolean validate(String password) {
//     boolean hasUppercase = false;
//     boolean hasDigit = false;
//     for (char c : password.toCharArray()) {
//       if (Character.isUpperCase(c)) {
//           hasUppercase = true;
//       }
//       if (Character.isDigit(c)) {
//           hasDigit = true;
//       }
//     }
//     return password.length() >= 8 && hasUppercase && hasDigit;
//   }
// }

// 3단계 리팩토링
// 각 규칙(길이, 대문자, 숫자)을 별도의 메서드로 분리하여 코드의 가독성 높임
public class PasswordValidator {
    public boolean validate(String password) {
        return isLengthValid(password) && hasUppercase(password) && hasDigit(password);
    }

    private boolean isLengthValid(String password) {
        return password != null && password.length() >= 8;
    }

    private boolean hasUppercase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private boolean hasDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }
}

