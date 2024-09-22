package org.example.repository.impl;

import org.example.db.ConnectionMyDB;
import org.example.model.Author;
import org.example.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.repository.impl.BookRepositoryImpl.AUTHOR_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
public class BookRepositoryImplTest {

    @Mock
    private ConnectionMyDB connectionMyDB;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BookRepositoryImpl bookRepository;

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this); // Инициализация моков

        // Настройка поведения для connectionMyDB
        when(connectionMyDB.getConnection()).thenReturn(connection);

        // Настройка поведения для connection
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Настройка поведения для preparedStatement
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);


        when(resultSet.next()).thenReturn(true); // предполагаем, что запись найдена
        when(resultSet.getInt(1)).thenReturn(1); // возвращаем 1, чтобы bookExists вернул true

        doNothing().when(resultSet).close();
        doNothing().when(preparedStatement).close();
        doNothing().when(connection).close();// Для методов, которые ожидают update
    }

    @Test
    public void testFindById_BookExists() throws Exception {
        // Мокируем необходимые объекты
        when(resultSet.next()).thenReturn(true, false); // Завершаем после первого вызова
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("title")).thenReturn("Test Book");
        when(resultSet.getInt(AUTHOR_ID)).thenReturn(1);

        // Мокируем поведение для получения подключения и выполнения запроса
        when(connectionMyDB.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Используем spy для репозитория, чтобы реально вызывать findById, но при этом мокировать внутренние зависимости
        BookRepositoryImpl spyRepository = spy(new BookRepositoryImpl(connectionMyDB));

        // Мокируем внутренний вызов findAuthorById
        doReturn(new Author(1, "Test Author")).when(spyRepository).findAuthorById(any(Connection.class), eq(1));

        // Выполняем тестируемый метод
        Book book = spyRepository.findById(1);

        // Проверки
        assertNotNull(book);
        assertEquals(1, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor().getName());

        // Проверка на закрытие ресурсов не требуется, если используется try-with-resources
    }

    @Test
    public void testFindById_BookDoesNotExist() throws Exception {
        // Подготовка для случая, когда книги нет
        when(resultSet.next()).thenReturn(false);

        Book book = bookRepository.findById(1);
        assertNull(book);
    }

    @Test
    public void testUpdate_Book() throws Exception {
        // Мокаем успешное обновление книги
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Author author = new Author(1, "Test Author");
        Book book = new Book(1, "Updated Book", author);

        Book updatedBook = bookRepository.update(book);

        assertEquals("Updated Book", updatedBook.getTitle());
    }

    @Test
    public void testDeleteById() throws Exception {
        // Мокаем успешное удаление книги
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = bookRepository.deleteById(1);

        assertTrue(result);
    }
}