package org.example.service.impl;

import org.example.model.Author;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.servlet.dto.AuthorDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorServiceTest {

    @Mock
    private AuthorRepositoryImpl authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGetAuthorById_Success() {
        Author author = new Author(1, "Test Author");
        when(authorRepository.findById(1)).thenReturn(author);

        AuthorDTO result = authorService.getAuthorById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Author", result.getName());
    }

    @Test
    public void testGetAllAuthors() {
        Author author1 = new Author(1, "Author One");
        Author author2 = new Author(2, "Author Two");
        when(authorRepository.findAll()).thenReturn(Arrays.asList(author1, author2));

        List<AuthorDTO> result = authorService.getAllAuthors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Author One", result.get(0).getName());
        assertEquals("Author Two", result.get(1).getName());
    }

    @Test
    public void testUpdateAuthor_Success() {
        Author existingAuthor = new Author(1, "Old Name");
        AuthorDTO authorDTO = new AuthorDTO(1, "Updated Name", Collections.emptyList());

        when(authorRepository.findById(1)).thenReturn(existingAuthor);

        authorService.updateAuthor(1, authorDTO);

        assertEquals("Updated Name", existingAuthor.getName());
        verify(authorRepository).update(existingAuthor);
    }

    @Test
    public void testUpdateAuthor_NotFound() {
        AuthorDTO authorDTO = new AuthorDTO(1, "Updated Name", Collections.emptyList());

        when(authorRepository.findById(1)).thenReturn(null);

        authorService.updateAuthor(1, authorDTO);

        verify(authorRepository, never()).update(any());
    }

    @Test
    public void testDeleteAuthor() {
        int authorId = 1;

        authorService.deleteAuthor(authorId);

        verify(authorRepository).deleteById(authorId);
    }
}