package org.example.repository.impl;
import org.example.db.ConnectionMyDB;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepositoryImpl implements BookRepository {
    private final ConnectionMyDB connection;
    static final String AUTHOR_ID = "author_id";
    private static final String TITLE = "title";
    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryImpl.class);
    private static final String ERROR = "An error occurred";

    public BookRepositoryImpl(ConnectionMyDB connection) {
        this.connection = connection;
    }

    public Book findById(Integer id) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Author author = findAuthorById(conn, rs.getInt(AUTHOR_ID));
                Book book = new Book(rs.getInt("id"), rs.getString(TITLE), author);
                book.setRelatedBooks(findRelatedBooks(id));
                return book;
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return null;
    }

    public Book save(Book book) {
        if (bookExists(book)) {
            throw new IllegalArgumentException("Book already exists");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        String query = "INSERT INTO books (title, author_id) VALUES (?, ?)";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting book failed, no rows affected.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    book.setId(rs.getInt(1));
                }
            }
            saveRelatedBooks(book);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving book: " + e.getMessage(), e);
        }
        return book;
    }

    boolean bookExists(Book book) {
        String query = "SELECT COUNT(*) FROM books WHERE title = ? AND author_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return false;
    }

    @Override
    public Book update(Book book) {
        String query = "UPDATE books SET title = ?, author_id = ? WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());
            stmt.setInt(3, book.getId());
            stmt.executeUpdate();
            deleteRelatedBooks(book.getId());
            saveRelatedBooks(book);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book", e);
        }
        return book;
    }

    public boolean deleteById(Integer id) {
        String query = "DELETE FROM books WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return true;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int bookId = rs.getInt("id");
                Author author = findAuthorById(conn, rs.getInt(AUTHOR_ID));
                Book book = new Book(bookId, rs.getString(TITLE), author);
                book.setRelatedBooks(findRelatedBooks(bookId));
                books.add(book);
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return books;
    }

    private List<Book> findRelatedBooks(int bookId) {
        List<Book> relatedBooks = new ArrayList<>();
        String query = "SELECT b.* FROM books b " +
                "JOIN book_relationships br ON b.id = br.related_book_id " +
                "WHERE br.book_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book foundBook = new Book(rs.getInt("id"), rs.getString(TITLE), findAuthorById(conn, rs.getInt(AUTHOR_ID)));
                relatedBooks.add(foundBook);
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return relatedBooks;
    }

    public void saveRelatedBooks(Book book) {
        if (book.getRelatedBooks() == null || book.getRelatedBooks().isEmpty()) {
            return;
        }
        deleteRelatedBooks(book.getId());

        String query = "INSERT INTO book_relationships (book_id, related_book_id) VALUES (?, ?)ON CONFLICT DO NOTHING";

        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Book relatedBook : book.getRelatedBooks()) {
                stmt.setInt(1, book.getId());
                stmt.setInt(2, relatedBook.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving related books: " + e.getMessage(), e);
        }
    }

    private void deleteRelatedBooks(int bookId) {
        String query = "DELETE FROM book_relationships WHERE book_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
    }

    Author findAuthorById(Connection conn, int authorId) {
        String query = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Author(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            logger.error(ERROR, e);
        }
        return null;
    }
}