package com.example;

import org.junit.jupiter.api.*;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스당 인스턴스 1개만 사용
public class LifecycleTest {
    public LifecycleTest() {
        System.out.println("new LifecycleTest");
    }

    @BeforeAll
    static void testSetUp() {
        System.out.println("setUp all test");
    }

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
    }

    @Test
    void a() {
        System.out.println("A");
    }

    @Test
    void b() {
        System.out.println("B");
    }

    @AfterEach
    void tearDown() {
        System.out.println("tearDown");
    }

    @AfterAll
    static void testTearDown() {
        System.out.println("after all test");
    }

}
