package org.example.servlet.mapper;

import org.example.model.Author;
import org.example.model.Book;
import org.example.servlet.dto.AuthorDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

public class AuthorMapperTest {

    @Test
    public void testToDTO_NullAuthor() {
        assertNull(AuthorMapper.toDTO(null));
    }

    @Test
    public void testToDTO_ValidAuthor() {
        Book mockBook1 = mock(Book.class);
        when(mockBook1.getTitle()).thenReturn("Book 1");

        Book mockBook2 = mock(Book.class);
        when(mockBook2.getTitle()).thenReturn("Book 2");

        Author author = new Author(1, "Author Name");
        author.setBooks(Arrays.asList(mockBook1, mockBook2));

        AuthorDTO authorDTO = AuthorMapper.toDTO(author);

        assertNotNull(authorDTO);
        assertEquals(1, authorDTO.getId());
        assertEquals("Author Name", authorDTO.getName());
        assertEquals(Arrays.asList("Book 1", "Book 2"), authorDTO.getBookTitles());
    }

    @Test
    public void testToEntity_NullAuthorDTO() {
        assertNull(AuthorMapper.toEntity(null));
    }

    @Test
    public void testToEntity_ValidAuthorDTO() {
        AuthorDTO authorDTO = new AuthorDTO(1, "Author Name", null);
        Author author = AuthorMapper.toEntity(authorDTO);

        assertNotNull(author);
        assertEquals(1, author.getId());
        assertEquals("Author Name", author.getName());
    }

    @Test
    public void testToEntity_AuthorDTOWithoutId() {
        AuthorDTO authorDTO = new AuthorDTO(null, "Author Name", null);
        Author author = AuthorMapper.toEntity(authorDTO);

        assertNotNull(author);
        assertEquals(0, author.getId());
        assertEquals("Author Name", author.getName());
    }
}
