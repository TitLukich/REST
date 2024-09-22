package org.example.servlet;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.db.ConnectionMyDB;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.service.impl.AuthorService;
import org.example.servlet.dto.AuthorDTO;

import java.io.IOException;
import java.util.List;

@WebServlet("/author")
public class AuthorServlet extends HttpServlet {
    AuthorService authorService;

    @Override
    public void init() {
        String jdbcUrl = getServletContext().getInitParameter("jdbc.url");
        String username = getServletContext().getInitParameter("jdbc.username");
        String password = getServletContext().getInitParameter("jdbc.password");

        ConnectionMyDB connectionMyDB = new ConnectionMyDB(jdbcUrl, username, password);
        authorService = new AuthorService(new AuthorRepositoryImpl(connectionMyDB));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            AuthorDTO authorDTO = authorService.getAuthorById(id);
            if (authorDTO != null) {
                resp.setContentType("application/json");
                resp.getWriter().write(new Gson().toJson(authorDTO));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            List<AuthorDTO> authors = authorService.getAllAuthors();
            resp.setContentType("application/json");
            resp.getWriter().write(new Gson().toJson(authors));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthorDTO authorDTO = new Gson().fromJson(req.getReader(), AuthorDTO.class);
        authorService.createAuthor(authorDTO);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        AuthorDTO authorDTO = new Gson().fromJson(req.getReader(), AuthorDTO.class);
        authorService.updateAuthor(id, authorDTO);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        authorService.deleteAuthor(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
