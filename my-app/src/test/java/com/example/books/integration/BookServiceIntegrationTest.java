package com.example.books.integration;

import com.example.books.Book;
import com.example.books.repository.InMemoryBookRepository;
import com.example.books.service.BookService;
import com.example.books.service.DuplicateBookException;
import com.example.books.service.BookNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("BookService 통합 테스트")
class BookServiceIntegrationTest {
    
    private BookService bookService;
    private InMemoryBookRepository bookRepository;
    
    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        bookService = new BookService(bookRepository);
        bookRepository.clear(); // 테스트 간 데이터 격리
    }
    
    @Test
    @Order(1)
    @DisplayName("도서 등록 후 검색이 가능하다")
    void registerAndFindBook() {
        // given
        Book book = new Book("978-1234567890", "클린 코드", "로버트 마틴");
        
        // when
        bookService.register(book);
        Optional<Book> found = bookService.findByIsbn("978-1234567890");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("클린 코드");
        assertThat(found.get().getAuthor()).isEqualTo("로버트 마틴");
    }
    
    @Test
    @Order(2)
    @DisplayName("존재하지 않는 도서 검색 시 빈 결과를 반환한다")
    void findNonExistentBook() {
        // when
        Optional<Book> found = bookService.findByIsbn("978-0000000000");
        
        // then
        assertThat(found).isEmpty();
    }
    
    @Test
    @Order(3)
    @DisplayName("제목으로 도서를 검색할 수 있다")
    void searchBooksByTitle() {
        // given
        bookService.register(new Book("978-1234567890", "클린 코드", "로버트 마틴"));
        bookService.register(new Book("978-0987654321", "클린 아키텍처", "로버트 마틴"));
        bookService.register(new Book("978-1111111111", "이펙티브 자바", "조슈아 블로크"));
        
        // when
        List<Book> result = bookService.searchByTitle("클린");
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title")
            .containsExactlyInAnyOrder("클린 코드", "클린 아키텍처");
    }
    
    @Test
    @Order(4)
    @DisplayName("저자명으로 도서를 검색할 수 있다")
    void searchBooksByAuthor() {
        // given
        bookService.register(new Book("978-1234567890", "클린 코드", "로버트 마틴"));
        bookService.register(new Book("978-0987654321", "클린 아키텍처", "로버트 마틴"));
        bookService.register(new Book("978-1111111111", "이펙티브 자바", "조슈아 블로크"));
        
        // when
        List<Book> result = bookService.searchByAuthor("로버트");
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("author")
            .containsOnly("로버트 마틴");
    }
    
    @Test
    @Order(5)
    @DisplayName("중복 도서 등록 시 예외가 발생한다")
    void registerDuplicateBook() {
        // given
        Book book1 = new Book("978-1234567890", "클린 코드", "로버트 마틴");
        Book book2 = new Book("978-1234567890", "다른 제목", "다른 저자");
        
        bookService.register(book1);
        
        // when & then
        assertThatThrownBy(() -> bookService.register(book2))
            .isInstanceOf(DuplicateBookException.class)
            .hasMessage("이미 등록된 도서입니다: 978-1234567890");
    }
    
    @Test
    @Order(6)
    @DisplayName("도서를 삭제할 수 있다")
    void removeBook() {
        // given
        Book book = new Book("978-1234567890", "클린 코드", "로버트 마틴");
        bookService.register(book);
        
        // when
        bookService.removeBook("978-1234567890");
        Optional<Book> found = bookService.findByIsbn("978-1234567890");
        
        // then
        assertThat(found).isEmpty();
    }
    
    @Test
    @Order(7)
    @DisplayName("존재하지 않는 도서 삭제 시 예외가 발생한다")
    void removeNonExistentBook() {
        // when & then
        assertThatThrownBy(() -> bookService.removeBook("978-0000000000"))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("존재하지 않는 도서입니다: 978-0000000000");
    }
    
    @Test
    @Order(8)
    @DisplayName("여러 도서 등록 후 전체 조회가 가능하다")
    void registerMultipleBooksAndFindAll() {
        // given
        bookService.register(new Book("978-1234567890", "클린 코드", "로버트 마틴"));
        bookService.register(new Book("978-0987654321", "클린 아키텍처", "로버트 마틴"));
        bookService.register(new Book("978-1111111111", "이펙티브 자바", "조슈아 블로크"));
        
        // when
        List<Book> allBooks = bookService.findAllBooks();
        
        // then
        assertThat(allBooks).hasSize(3);
        assertThat(allBooks).extracting("isbn")
            .containsExactlyInAnyOrder(
                "978-1234567890", 
                "978-0987654321", 
                "978-1111111111"
            );
    }
    
    @Test
    @Order(9)
    @DisplayName("대소문자 구분 없이 검색이 가능하다")
    void caseInsensitiveSearch() {
        // given
        bookService.register(new Book("978-1234567890", "Clean Code", "Robert Martin"));
        
        // when
        List<Book> titleResult = bookService.searchByTitle("clean");
        List<Book> authorResult = bookService.searchByAuthor("robert");
        
        // then
        assertThat(titleResult).hasSize(1);
        assertThat(authorResult).hasSize(1);
    }
}