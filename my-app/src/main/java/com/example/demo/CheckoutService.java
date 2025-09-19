package com.example.demo;

import com.example.user.User;

public class CheckoutService {

    private final PaymentGateway gateway;

    public CheckoutService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public boolean pay(User user, double amount) {
        // 실제 결제 연동은 생략, 게이트웨이에 위임
        return gateway.charge(amount);
    }
}
