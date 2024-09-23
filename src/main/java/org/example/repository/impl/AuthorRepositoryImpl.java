package org.example.repository.impl;

import org.example.db.ConnectionMyDB;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorRepositoryImpl implements AuthorRepository {
    private final ConnectionMyDB connection;
    private static final Logger logger = LoggerFactory.getLogger(AuthorRepositoryImpl.class);
    private static final String ERROR = "An error occurred";

    public AuthorRepositoryImpl(ConnectionMyDB connection) {
        this.connection = connection;
    }

    public Author findById(Integer id) {
        String query = "SELECT * FROM authors WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Author author = new Author(rs.getInt("id"), rs.getString("name"));
                author.setBooks(findBooksByAuthorId(id)); // Загружаем книги автора
                return author;
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return null;
    }

    public List<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int authorId = rs.getInt("id");
                Author author = new Author(authorId, rs.getString("name"));
                List<Book> books = findBooksByAuthorId(authorId);
                author.setBooks(books);
                authors.add(author);
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return authors;
    }

    public Author save(Author author) {
        String query = "INSERT INTO authors (name) VALUES (?)";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                author.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return author;
    }

    @Override
    public Author update(Author author) {
        String query = "UPDATE authors SET name = ? WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, author.getName());
            stmt.setInt(2, author.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating author", e);
        }
        return author;
    }

    public boolean deleteById(Integer id) {
        String query = "DELETE FROM authors WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return false;
    }

    private List<Book> findBooksByAuthorId(Integer authorId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT id, title FROM books WHERE author_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), null);
                books.add(book);
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return books;
    }
}