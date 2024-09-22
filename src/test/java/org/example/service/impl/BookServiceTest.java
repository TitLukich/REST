package org.example.service.impl;

import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.servlet.dto.BookDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepositoryImpl bookRepository;

    @Mock
    private AuthorRepositoryImpl authorRepository;

    @InjectMocks
    private BookService bookService;

    private Author mockAuthor;
    private Book mockBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockAuthor = new Author(1, "Test Author");
        mockBook = new Book(1, "Test Book", mockAuthor);
    }

    @Test
    public void testGetBookById_BookExists() {
        when(bookRepository.findById(1)).thenReturn(mockBook);

        BookDTO result = bookService.getBookById(1);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(mockAuthor.getId(), result.getAuthorId());
    }

    @Test
    public void testGetBookById_BookDoesNotExist() {
        when(bookRepository.findById(1)).thenReturn(null);

        BookDTO result = bookService.getBookById(1);

        assertNull(result);
    }

    @Test
    public void testCreateBook_Success() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Book");
        bookDTO.setAuthorId(1);
        bookDTO.setRelatedBookIds(new ArrayList<>());

        when(authorRepository.findById(1)).thenReturn(mockAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        bookService.createBook(bookDTO);

        verify(bookRepository).save(any(Book.class));
        assertEquals(mockAuthor.getBooks().size(), 1); // Проверка, что книга добавлена к автору
    }

    @Test
    public void testCreateBook_InvalidTitle() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(null);
        bookDTO.setAuthorId(1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(bookDTO);
        });
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testUpdateBook_Success() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Updated Book");
        bookDTO.setAuthorId(1);
        bookDTO.setRelatedBookIds(new ArrayList<>());

        when(authorRepository.findById(1)).thenReturn(mockAuthor);
        when(bookRepository.findById(1)).thenReturn(mockBook);

        bookService.updateBook(1, bookDTO);

        assertEquals("Updated Book", mockBook.getTitle());
        verify(bookRepository).update(mockBook);
    }

    @Test
    public void testUpdateBook_AuthorNotFound() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Updated Book");
        bookDTO.setAuthorId(2);

        when(authorRepository.findById(2)).thenReturn(null); // Настройка мока

        Book existingBook = new Book();
        existingBook.setId(1);
        existingBook.setTitle("Original Book");
        when(bookRepository.findById(1)).thenReturn(existingBook);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBook(1, bookDTO);
        });

        assertEquals("Author not found for ID: 2", exception.getMessage());
    }

    @Test
    public void testDeleteBook_Success() {
        bookService.deleteBook(1);
        verify(bookRepository).deleteById(1);
    }

    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(mockBook));

        List<BookDTO> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockBook.getTitle(), result.get(0).getTitle());
    }
}
