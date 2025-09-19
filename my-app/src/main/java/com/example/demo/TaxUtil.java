// TaxUtil.java (정적 메서드 모킹 예제용)
package com.example.demo;
public class TaxUtil {
    public static double rateFor(String country){
        if("KR".equalsIgnoreCase(country)) return 0.10;
        if("US".equalsIgnoreCase(country)) return 0.07;
        return 0.0;
    }
}
