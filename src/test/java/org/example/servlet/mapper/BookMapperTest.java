package org.example.servlet.mapper;

import org.example.model.Author;
import org.example.model.Book;
import org.example.servlet.dto.BookDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;

public class BookMapperTest {

    @Test
    public void testToDTO_NullBook() {
        assertNull(BookMapper.toDTO(null));
    }

    @Test
    public void testToDTO_ValidBook() {

        Author mockAuthor = mock(Author.class);
        when(mockAuthor.getId()).thenReturn(1);

        Book mockBook = mock(Book.class);
        when(mockBook.getId()).thenReturn(2);
        when(mockBook.getTitle()).thenReturn("Book Title");
        when(mockBook.getAuthor()).thenReturn(mockAuthor);

        Book relatedBook1 = mock(Book.class);
        when(relatedBook1.getId()).thenReturn(3);

        Book relatedBook2 = mock(Book.class);
        when(relatedBook2.getId()).thenReturn(4);

        when(mockBook.getRelatedBooks()).thenReturn(Arrays.asList(relatedBook1, relatedBook2));

        BookDTO bookDTO = BookMapper.toDTO(mockBook);

        assertNotNull(bookDTO);
        assertEquals(2, bookDTO.getId());
        assertEquals("Book Title", bookDTO.getTitle());
        assertEquals(1, bookDTO.getAuthorId());
        assertEquals(Arrays.asList(3, 4), bookDTO.getRelatedBookIds());
    }

    @Test
    public void testToEntity_NullBookDTO() {

        assertNull(BookMapper.toEntity(null, null, null));
    }

    @Test
    public void testToEntity_ValidBookDTO() {
        Author mockAuthor = mock(Author.class);
        when(mockAuthor.getId()).thenReturn(1);

        BookDTO bookDTO = new BookDTO(2, "Book Title", 1, Arrays.asList(3, 4));

        Book book = BookMapper.toEntity(bookDTO, mockAuthor, Arrays.asList());

        assertNotNull(book);
        assertEquals(2, book.getId());
        assertEquals("Book Title", book.getTitle());
        assertEquals(mockAuthor, book.getAuthor());
        assertTrue(book.getRelatedBooks().isEmpty());
    }

    @Test
    public void testToEntity_WithRelatedBooks() {
        Author mockAuthor = mock(Author.class);
        when(mockAuthor.getId()).thenReturn(1);

        BookDTO bookDTO = new BookDTO(2, "Book Title", 1, Arrays.asList(3, 4));

        Book relatedBook1 = mock(Book.class);
        when(relatedBook1.getId()).thenReturn(3);

        Book relatedBook2 = mock(Book.class);
        when(relatedBook2.getId()).thenReturn(4);

        Book book = BookMapper.toEntity(bookDTO, mockAuthor, Arrays.asList(relatedBook1, relatedBook2));

        assertNotNull(book);
        assertEquals(2, book.getId());
        assertEquals("Book Title", book.getTitle());
        assertEquals(mockAuthor, book.getAuthor());
        assertEquals(2, book.getRelatedBooks().size());
        assertTrue(book.getRelatedBooks().contains(relatedBook1));
        assertTrue(book.getRelatedBooks().contains(relatedBook2));
    }
}