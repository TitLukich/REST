package org.example.repository.impl;

import org.example.db.ConnectionMyDB;
import org.example.model.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class AuthorRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private AuthorRepositoryImpl authorRepository;
    private ConnectionMyDB connection;

    @BeforeEach
    void setUp() {
        connection = new ConnectionMyDB(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        authorRepository = new AuthorRepositoryImpl(connection);

        // Создание таблицы для авторов
        createTable();
    }

    @AfterEach
    void tearDown() {
        // Удаление таблицы для авторов
        dropTable();
    }

    private void createTable() {
        String query = "CREATE TABLE authors (id SERIAL PRIMARY KEY, name VARCHAR(255));";
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropTable() {
        String query = "DROP TABLE IF EXISTS authors;";
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndFindById() {
        Author author = new Author(0, "Cергей Лукьяненко");
        authorRepository.save(author);

        Author foundAuthor = authorRepository.findById(author.getId());
        assertNotNull(foundAuthor);
        assertEquals("Cергей Лукьяненко", foundAuthor.getName());
    }

    @Test
    void testFindAll() {
        authorRepository.save(new Author(0, "Author 1"));
        authorRepository.save(new Author(0, "Author 2"));

        List<Author> authors = authorRepository.findAll();
        assertEquals(2, authors.size());
    }

    @Test
    void testUpdate() {
        Author author = authorRepository.save(new Author(0, "Cергей Лукьяненко"));
        author.setName("Новый Имя");

        authorRepository.update(author);
        Author updatedAuthor = authorRepository.findById(author.getId());
        assertEquals("Новый Имя", updatedAuthor.getName());
    }

    @Test
    void testDeleteById() {
        Author author = authorRepository.save(new Author(0, "Cергей Лукьяненко"));
        boolean deleted = authorRepository.deleteById(author.getId());

        assertTrue(deleted);
        assertNull(authorRepository.findById(author.getId()));
    }
}