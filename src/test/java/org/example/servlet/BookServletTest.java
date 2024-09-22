package org.example.servlet;

import static org.mockito.Mockito.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.impl.BookService;
import org.example.servlet.dto.BookDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collections;

public class BookServletTest {

    private BookServlet bookServlet;

    @Mock
    private BookService mockBookService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private PrintWriter mockWriter;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        bookServlet = new BookServlet();
        bookServlet.setBookService(mockBookService);

        when(mockResponse.getWriter()).thenReturn(mockWriter);
    }
    @Test
    public void testDoPost_ValidBook() throws Exception {
        String bookJson = "{\"title\":\"New Book\",\"authorId\":1}";
        BufferedReader reader = new BufferedReader(new StringReader(bookJson));
        when(mockRequest.getReader()).thenReturn(reader);

        bookServlet.doPost(mockRequest, mockResponse);

        verify(mockBookService).createBook(any(BookDTO.class));
        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoPost_InvalidBookData() throws Exception {
        String bookJson = "{\"title\":null,\"authorId\":0}";
        BufferedReader reader = new BufferedReader(new StringReader(bookJson));
        when(mockRequest.getReader()).thenReturn(reader);

        bookServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_InvalidJsonFormat() throws Exception {
        String invalidJson = "invalid json";
        BufferedReader reader = new BufferedReader(new StringReader(invalidJson));
        when(mockRequest.getReader()).thenReturn(reader);
        bookServlet.doPost(mockRequest, mockResponse);
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_ValidId() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockBookService.getBookById(1)).thenReturn(new BookDTO(1, "Book Title", 1, Collections.emptyList()));
        bookServlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).setContentType("application/json");

    }

    @Test
    public void testDoGet_InvalidIdFormat() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("invalid");

        bookServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_BookNotFound() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockBookService.getBookById(1)).thenReturn(null);

        bookServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDoGet_AllBooks() throws Exception {
        when(mockBookService.getAllBooks()).thenReturn(Collections.singletonList(new BookDTO(1, "Book Title", 1, Collections.emptyList())));

        bookServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json");

    }

    @Test
    public void testDoPut_ValidBook() throws Exception {
        String bookJson = "{\"title\":\"Updated Book\",\"authorId\":1}";
        BufferedReader reader = new BufferedReader(new StringReader(bookJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getParameter("id")).thenReturn("1");

        bookServlet.doPut(mockRequest, mockResponse);

        verify(mockBookService).updateBook(eq(1), any(BookDTO.class));
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoPut_InvalidIdFormat() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("invalid");

        bookServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_InvalidBookData() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("1");
        String bookJson = "{\"title\":null,\"authorId\":0}";
        BufferedReader reader = new BufferedReader(new StringReader(bookJson));
        when(mockRequest.getReader()).thenReturn(reader);

        bookServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_ValidId() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("1");

        bookServlet.doDelete(mockRequest, mockResponse);

        verify(mockBookService).deleteBook(1);
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_InvalidIdFormat() throws Exception {
        when(mockRequest.getParameter("id")).thenReturn("invalid");

        bookServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}