package org.example.servlet;

import com.google.gson.Gson;
import org.example.service.impl.AuthorService;
import org.example.servlet.dto.AuthorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthorServletTest {

    private AuthorServlet authorServlet;
    private AuthorService mockAuthorService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private PrintWriter mockWriter;

    @BeforeEach
    public void setUp() throws IOException {
        mockAuthorService = mock(AuthorService.class);
        authorServlet = new AuthorServlet();

        authorServlet.authorService = mockAuthorService;

        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockWriter = mock(PrintWriter.class);

        when(mockResponse.getWriter()).thenReturn(mockWriter);
    }

    @Test
    public void testDoGet_WithId() throws Exception {

        AuthorDTO mockAuthor = new AuthorDTO(1, "Author Name", Arrays.asList("Book 1", "Book 2"));
        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockAuthorService.getAuthorById(1)).thenReturn(mockAuthor);

        authorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json");
        verify(mockWriter).write(new Gson().toJson(mockAuthor));
    }

    @Test
    public void testDoGet_WithoutId() throws Exception {

        List<AuthorDTO> authors = Arrays.asList(
                new AuthorDTO(1, "Author 1", Arrays.asList("Book 1")),
                new AuthorDTO(2, "Author 2", Arrays.asList("Book 2"))
        );
        when(mockRequest.getParameter("id")).thenReturn(null);
        when(mockAuthorService.getAllAuthors()).thenReturn(authors);


        authorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json");
        verify(mockWriter).write(new Gson().toJson(authors));
    }

    @Test
    public void testDoGet_AuthorNotFound() throws Exception {

        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockAuthorService.getAuthorById(1)).thenReturn(null);

        authorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDoPost() throws Exception {
        // Подготовка
        String authorJson = "{\"id\":1,\"name\":\"New Author\",\"bookTitles\":[\"Book 1\"]}";
        BufferedReader reader = new BufferedReader(new StringReader(authorJson));
        when(mockRequest.getReader()).thenReturn(reader);

        authorServlet.doPost(mockRequest, mockResponse);

        verify(mockAuthorService).createAuthor(argThat(authorDTO ->
                authorDTO.getId() == 1 &&
                        "New Author".equals(authorDTO.getName()) &&
                        authorDTO.getBookTitles().contains("Book 1")
        ));
        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoPut() throws Exception {

        String authorJson = "{\"id\":1,\"name\":\"Updated Author\",\"bookTitles\":[\"Book 2\"]}";
        BufferedReader reader = new BufferedReader(new StringReader(authorJson));
        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockRequest.getReader()).thenReturn(reader);

        authorServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<AuthorDTO> authorDTOCaptor = ArgumentCaptor.forClass(AuthorDTO.class);
        verify(mockAuthorService).updateAuthor(eq(1), authorDTOCaptor.capture());

        AuthorDTO capturedAuthorDTO = authorDTOCaptor.getValue();
        assertEquals(1, capturedAuthorDTO.getId());
        assertEquals("Updated Author", capturedAuthorDTO.getName());
        assertEquals(Arrays.asList("Book 2"), capturedAuthorDTO.getBookTitles());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoDelete() throws Exception {

        when(mockRequest.getParameter("id")).thenReturn("1");

        authorServlet.doDelete(mockRequest, mockResponse);

        verify(mockAuthorService).deleteAuthor(1);
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
