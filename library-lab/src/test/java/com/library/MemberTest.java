package com.library;

import com.library.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberTest {

    @InjectMocks
    private Member member = new Member("M001", "김개발");

    @ParameterizedTest
    @CsvSource({
        "M001, 김개발",
        "M002, 박테스트",
        "M003, 이자바"
    })
    void testMemberCreation(String id, String name) {
        // TODO: Member 생성 후 id와 name이 null/빈문자가 아닌지 검증
    }

    @ParameterizedTest
    @CsvSource({
        "1",
        "2",
        "3"
    })

    @Test
    void testBorrowBookWithinLimit() {
        // TODO: borrowBook() 호출
        // TODO: getBorrowedBooksCount() == 1 검증
    }

    @Test
    void testBorrowExceedsLimit() {
        // TODO: 3번 borrowBook() 호출
        // TODO: 4번째 호출 시 예외 발생 검증
    }

    @Test
    void testReturnBook() {
        // TODO: borrowBook() → returnBook() 호출
        // TODO: getBorrowedBooksCount() == 0 검증
    }
}
