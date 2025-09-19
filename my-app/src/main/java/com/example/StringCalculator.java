package com.example;

// import java.beans.IntrospectionException;

public class StringCalculator {
  // 1) 빈 문자열은 ZERO return 기능 구현
  // public int add(String s){
  //   if (s == null || s.isBlank()) return 0;

  //   return Integer.parseInt(s.trim());
  // }

  // 2) 콤마(,)로 구분된 수 더하기 기능 구현
  // public int add(String s){
  //   if (s == null || s.isBlank()) return 0;

  //   String[] parts = s.split(",");
  //   int sum = 0;
  //   for (String p: parts) sum += Integer.parseInt(p.trim());

  //   return sum;
  // }

  // 3) 줄바꿈(\n)도 구분자로 허용하는 기능 구현
  // public int add(String s){
  //   if (s == null || s.isBlank()) return 0;
  //   String[] parts = s.split(",|\n");
  //   int sum = 0;
  //   for (String p: parts) sum += Integer.parseInt(p.trim());

  //   return sum;
  // }

  // 4) 커스텀 구분자("//")가 있으면 커스텀 구분자로 split 하는 기능 구현
  // public int add(String s){
  //   if (s == null || s.isBlank()) return 0;

  //   String delimiters = ",|\n";

  //   // "//;\n1;2;3" -> "1;2;3" 으로 분리하고자 함
  //   if (s.startsWith("//")){
  //     int nl = s.indexOf("\n");
  //     String header = s.substring(2, nl);
  //     delimiters = java.util.regex.Pattern.quote(header);  // 특수문자를 정규식으로 변환
  //     s = s.substring(nl + 1);  // 숫자만 잘라냄
  //   }

  //   String[] parts = s.split(delimiters);
  //   int sum = 0;
  //   for (String p: parts) sum += Integer.parseInt(p.trim());

  //   return sum;
  // }

  // 5) 음수 포함 시 예외(모든 음수 표시): "negatives not allowed: -2,-5"
  // public int add(String s) {
  //   if (s == null || s.isBlank()) return 0;
  //   String delimiters = ",|\n";
  //   if (s.startsWith("//")) {
  //     int nl = s.indexOf('\n');
  //     String header = s.substring(2, nl);
  //     delimiters = java.util.regex.Pattern.quote(header);
  //     s = s.substring(nl + 1);
  //   }
  //   String[] parts = s.split(delimiters);
    
  //   java.util.List<Integer> negatives = new java.util.ArrayList<>(); 
  //   int sum = 0;
  //   for (String p : parts) {
  //     if (p.isBlank()) continue;
  //     int n = Integer.parseInt(p.trim());
  //     if (n < 0) negatives.add(n);
  //     sum += n;
  //   }
  //   if (!negatives.isEmpty()) {
  //     throw new IllegalArgumentException("negatives not allowed: " + negatives);
  //   }
  //   return sum;
  // }
  

// Refactoring 
  public int add(String s) {
    if (s == null || s.isBlank()) return 0;
    String[] parts = splitWithDelimiters(s);
    java.util.List<Integer> negatives = new java.util.ArrayList<>();
    int sum = 0;
    for (String p : parts) {
      if (p.isBlank()) continue;
      int n = Integer.parseInt(p.trim());
      if (n < 0) negatives.add(n);
      sum += n;
    }
    if (!negatives.isEmpty())
      throw new IllegalArgumentException("negatives not allowed: " + negatives);
    return sum;
  }

  private String[] splitWithDelimiters(String s) {
    String delimiters = ",|\n";
    if (s.startsWith("//")) {
      int nl = s.indexOf('\n');
      String header = s.substring(2, nl);
      delimiters = java.util.regex.Pattern.quote(header);
      s = s.substring(nl + 1);
    }
    return s.split(delimiters);
  }
}