package com.example.books.service;

import com.example.books.Book;
import com.example.books.repository.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService 테스트")
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    private BookService bookService;
    
    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }
    
    @Test
    @DisplayName("새로운 도서를 정상적으로 등록할 수 있다")
    void registerNewBook() {
        // given
        Book book = new Book("978-1234567890", "클린 코드", "로버트 마틴");
        when(bookRepository.existsByIsbn("978-1234567890")).thenReturn(false);
        
        // when
        bookService.register(book);
        
        // then
        verify(bookRepository).existsByIsbn("978-1234567890");
        verify(bookRepository).save(book);
    }
    
    @Test
    @DisplayName("중복된 ISBN의 도서 등록 시 예외가 발생한다")
    void registerDuplicateBook() {
        // given
        String isbn = "978-1234567890";
        Book book = new Book(isbn, "제목", "저자");
        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> bookService.register(book))
            .isInstanceOf(DuplicateBookException.class)
            .hasMessage("이미 등록된 도서입니다: " + isbn);
            
        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("ISBN으로 도서를 찾을 수 있다")
    void findBookByIsbn() {
        // given
        String isbn = "978-1234567890";
        Book expectedBook = new Book(isbn, "클린 코드", "로버트 마틴");
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(expectedBook));
        
        // when
        Optional<Book> result = bookService.findByIsbn(isbn);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedBook);
        verify(bookRepository).findByIsbn(isbn);
    }
    
    @Test
    @DisplayName("존재하지 않는 ISBN으로 검색 시 빈 결과를 반환한다")
    void findNonExistentBook() {
        // given
        String isbn = "978-0000000000";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
        
        // when
        Optional<Book> result = bookService.findByIsbn(isbn);
        
        // then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("null 또는 빈 ISBN으로 검색 시 예외가 발생한다")
    void findBookWithInvalidIsbn() {
        // when & then
        assertThatThrownBy(() -> bookService.findByIsbn(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN이 필요합니다");
            
        assertThatThrownBy(() -> bookService.findByIsbn(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN이 필요합니다");
            
        assertThatThrownBy(() -> bookService.findByIsbn("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN이 필요합니다");
    }
    
    @Test
    @DisplayName("제목으로 도서를 검색할 수 있다")
    void searchBooksByTitle() {
        // given
        String title = "클린";
        List<Book> expectedBooks = Arrays.asList(
            new Book("978-1234567890", "클린 코드", "로버트 마틴"),
            new Book("978-0987654321", "클린 아키텍처", "로버트 마틴")
        );
        when(bookRepository.findByTitle(title)).thenReturn(expectedBooks);
        
        // when
        List<Book> result = bookService.searchByTitle(title);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedBooks);
        verify(bookRepository).findByTitle(title);
    }
    
    @Test
    @DisplayName("저자명으로 도서를 검색할 수 있다")
    void searchBooksByAuthor() {
        // given
        String author = "로버트 마틴";
        List<Book> expectedBooks = Arrays.asList(
            new Book("978-1234567890", "클린 코드", "로버트 마틴"),
            new Book("978-0987654321", "클린 아키텍처", "로버트 마틴")
        );
        when(bookRepository.findByAuthor(author)).thenReturn(expectedBooks);
        
        // when
        List<Book> result = bookService.searchByAuthor(author);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedBooks);
        verify(bookRepository).findByAuthor(author);
    }
    
    @Test
    @DisplayName("도서를 삭제할 수 있다")
    void removeBook() {
        // given
        String isbn = "978-1234567890";
        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);
        
        // when
        bookService.removeBook(isbn);
        
        // then
        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository).deleteByIsbn(isbn);
    }
    
    @Test
    @DisplayName("존재하지 않는 도서 삭제 시 예외가 발생한다")
    void removeNonExistentBook() {
        // given
        String isbn = "978-0000000000";
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        
        // when & then
        assertThatThrownBy(() -> bookService.removeBook(isbn))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("존재하지 않는 도서입니다: " + isbn);
            
        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository, never()).deleteByIsbn(any());
    }
}