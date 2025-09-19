package com.example.demo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.user.User;
import com.example.user.UserRepository;
import com.example.user.UserService;
import com.example.user.EmailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

/**
 * Mockito 객체 생성 및 다양한 기능 테스트 예제
 */
@ExtendWith(MockitoExtension.class) // ✅ @Mock/@InjectMocks 필드 자동 초기화
class MockitoCreationExamplesTest {

    // @Mock → UserRepository를 가짜(Mock) 객체로 생성
    @Mock UserRepository repo;

    // @Mock → EmailSender를 가짜(Mock) 객체로 생성
    @Mock EmailSender mail;

    // @InjectMocks → 위에서 만든 repo, mail을 UserService에 주입
    @InjectMocks UserService service;

    // 각 테스트 시작 전에 실행 → 어떤 테스트가 시작되는지 콘솔에 출력
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("\n▶ [테스트 시작] " + testInfo.getDisplayName());
    }

    /**
     * UserService.register() 실행 시
     *  - repo.save() 동작을 스텁
     *  - 메일 발송 검증
     */
    @Test
    @DisplayName("UserService.register() → 저장/메일 호출 검증")
    void annotation_based_mock_and_injection() {
        // repo.save(any()) 호출 시 ID=1 User 반환하도록 스텁
        when(repo.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(1L, u.getEmail(), u.getName());
        });

        // UserService.register() 실행
        User saved = service.register("kim@example.com","Kim");

        // 저장된 user의 ID 확인
        assertEquals(1L, saved.getId());
        // 저장 메서드 호출 검증
        verify(repo).save(any(User.class));
        // 메일 발송 검증
        verify(mail).sendWelcomeEmail(saved);
        // 그 외 불필요한 호출 없음을 확인
        verifyNoMoreInteractions(repo, mail);
    }

    /**
     * mock() 메서드를 직접 호출해 객체를 만드는 기본 사용법
     */
    @Test
    @DisplayName("직접 mock() 호출 → findById 동작 검증")
    void basic_mock_creation_with_Mockito_mock() {
        // 직접 mock() 호출로 UserRepository 생성
        UserRepository localRepo = mock(UserRepository.class);

        // findById(10L) 호출 시 특정 User 반환
        when(localRepo.findById(10L))
                .thenReturn(Optional.of(new User(10L,"a@b.c","A")));

        // 실제 동작 확인
        Optional<User> found = localRepo.findById(10L);
        assertTrue(found.isPresent());

        // 호출 검증
        verify(localRepo).findById(10L);
    }

    /**
     * spy() → 실제 객체 기반 Mock
     *  - 실제 add() 메서드는 실행됨
     *  - size() 는 우리가 스텁한 값으로 동작
     */
    @Test
    @DisplayName("spy 사용 → 실제 동작 + 일부만 스텁")
    void spy_creation_partial_mock() {
        // ArrayList 기반 spy 생성
        List<String> spyList = spy(new ArrayList<>());

        // 실제 add() 호출됨
        spyList.add("a");
        spyList.add("b");

        // size() 결과만 100으로 덮어쓰기
        doReturn(100).when(spyList).size();

        // 검증
        assertEquals(100, spyList.size());
        verify(spyList).add("a");
        verify(spyList).add("b");
    }

    /**
     * RETURNS_DEEP_STUBS 사용 예제
     *  - 다단계 체인 호출(node.child().child().name()) 스텁 가능
     */
    @Test
    @DisplayName("RETURNS_DEEP_STUBS → 체이닝된 mock 호출")
    void withSettings_and_deep_stubs() {
        interface Node { Node child(); String name(); }

        // 체인 호출 가능한 mock 생성
        Node node = mock(Node.class, RETURNS_DEEP_STUBS);

        // node.child().child().name() 호출 시 "leaf" 반환
        when(node.child().child().name()).thenReturn("leaf");

        assertEquals("leaf", node.child().child().name());
    }

    /**
     * ArgumentCaptor 사용
     *  - sendNewsletter() 호출 시 실제 전달된 인자 값까지 검증
     */
    @Test
    @DisplayName("ArgumentCaptor → 전달된 메일 값 검증")
    void argument_captor_and_matchers() {
        // repo.findById() 호출 시 특정 User 반환
        when(repo.findById(anyLong()))
                .thenReturn(Optional.of(new User(2L,"x@y.z","X")));

        // 뉴스레터 발송 실행
        boolean ok = service.sendNewsletter(2L);
        assertTrue(ok);

        // ArgumentCaptor 로 실제 전달된 값 캡쳐
        ArgumentCaptor<String> to = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        // mail.send(...) 호출 시 캡쳐
        verify(mail).send(to.capture(), subject.capture(), body.capture());

        // 값 검증
        assertEquals("x@y.z", to.getValue());
        assertEquals("News", subject.getValue());
        assertTrue(body.getValue().contains("Hello X"));
    }

    /**
     * BDD 스타일 문법 예제
     * given/when/then 패턴으로 작성
     */
    @Test
    @DisplayName("BDD 스타일 → given/when/then")
    void bdd_style_given_when_then() {
        // Mock 객체 생성
        PaymentGateway gateway = mock(PaymentGateway.class);
        CheckoutService checkout = new CheckoutService(gateway);
        User user = new User(99L, "u@ex.com", "U");

        // given
        given(gateway.charge(anyDouble())).willReturn(true);

        // when
        boolean paid = checkout.pay(user, 49.9);

        // then
        assertTrue(paid);
        then(gateway).should().charge(eq(49.9));
    }

    /**
     * 정적 메서드 모킹 예제
     * (mockito-inline 의존성 필요)
     */
    @Test
    @DisplayName("정적 메서드 모킹 (try-with-resources)")
    void static_method_mocking_try_with_resources() {
        try (MockedStatic<TaxUtil> mocked = mockStatic(TaxUtil.class)) {
            // TaxUtil.rateFor("KR") 호출 시 0.20 반환
            mocked.when(() -> TaxUtil.rateFor("KR")).thenReturn(0.20);

            assertEquals(0.20, TaxUtil.rateFor("KR"));
            System.out.println("➡️ 블록 내: KR 세율 0.20");
        }
        // 블록 벗어나면 원래 구현(0.10) 복원
        assertEquals(0.10, TaxUtil.rateFor("KR"));
        System.out.println("➡️ 블록 외: KR 세율 원래 값 0.10");
    }

    /**
     * mock 생성 시 withSettings 옵션 사용
     *  - 이름 부여, SMART_NULLS 로 NPE 방지
     */
    @Test
    @DisplayName("withSettings 옵션 → 이름 부여, SMART_NULLS 사용")
    void mock_with_defaultAnswer_and_name() {
        // Foo 인터페이스 mock 생성, SMART_NULLS 적용
        Foo foo = mock(Foo.class,
                withSettings()
                        .name("FooMock")
                        .defaultAnswer(Answers.RETURNS_SMART_NULLS));

        // SMART_NULLS 덕분에 NPE 대신 기본 값 반환
        assertNotNull(foo.toString());
    }

    // 단순 인터페이스 예제
    interface Foo { String hello(); }
}
