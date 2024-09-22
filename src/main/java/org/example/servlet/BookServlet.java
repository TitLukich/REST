package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionMyDB;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.service.impl.BookService;
import org.example.servlet.dto.BookDTO;

import java.io.IOException;
import java.util.List;

@WebServlet("/book")
public class BookServlet extends HttpServlet {
    private transient BookService bookService;
    private static final String SERVER_ERROR = "\"{\\\"error\\\":\\\"Server error\\\"}\"";
    private static final String INVALID_ID_FORMAT = "{\"error\":\"Invalid ID format\"}";

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void init() {
        String jdbcUrl = getServletContext().getInitParameter("jdbc.url");
        String username = getServletContext().getInitParameter("jdbc.username");
        String password = getServletContext().getInitParameter("jdbc.password");

        ConnectionMyDB connectionMyDB = new ConnectionMyDB(jdbcUrl, username, password);
        bookService = new BookService(new BookRepositoryImpl(connectionMyDB), new AuthorRepositoryImpl(connectionMyDB));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {

                int id = Integer.parseInt(idParam);
                BookDTO bookDTO = bookService.getBookById(id);
                if (bookDTO != null) {
                    resp.setContentType("application/json");
                    resp.getWriter().write(new Gson().toJson(bookDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {

                List<BookDTO> books = bookService.getAllBooks();
                resp.setContentType("application/json");
                resp.getWriter().write(new Gson().toJson(books));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_ID_FORMAT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BookDTO bookDTO = new Gson().fromJson(req.getReader(), BookDTO.class);
            if (bookDTO == null || bookDTO.getTitle() == null || bookDTO.getAuthorId() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid book data\"}");
                return;
            }
            bookService.createBook(bookDTO);
            System.out.println("Created book: " + bookDTO.getTitle());
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write( "{\"" + SERVER_ERROR + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing or invalid ID parameter\"}");
                return;
            }
            int id = Integer.parseInt(idParam);
            BookDTO bookDTO = new Gson().fromJson(req.getReader(), BookDTO.class);
            if (bookDTO == null || bookDTO.getTitle() == null || bookDTO.getAuthorId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid book data\"}");
                return;
            }
            bookService.updateBook(id, bookDTO);
            System.out.println("Updated book ID: " + id + " with new title: " + bookDTO.getTitle());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_ID_FORMAT);
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing or invalid ID parameter\"}");
                return;
            }
            int id = Integer.parseInt(idParam);
            bookService.deleteBook(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(INVALID_ID_FORMAT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(SERVER_ERROR);
            e.printStackTrace();
        }
    }
}