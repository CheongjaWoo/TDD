package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MockitoTest {

    @Test
    public void testVerify() {
        // mock 생성, 기록만 남음
        List<String> mockedList = mock(List.class);

        // 동작
        mockedList.add("one");
        mockedList.clear();

        // 검증
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    @Test
    void testStubAndMock() {
        // mock 생성
        List<String> mockList = mock(List.class);

        // stubbing (행동 정의)
        when(mockList.get(0)).thenReturn("FirstOne");
        when(mockList.get(999)).thenThrow(new RuntimeException());

        // 동작 확인
        assertEquals("FirstOne", mockList.get(0));

        // 실행 검증
        verify(mockList).get(0);

        // 예외 확인
        assertThrows(RuntimeException.class, () -> mockList.get(999));
    }
}

