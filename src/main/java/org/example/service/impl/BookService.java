package org.example.service.impl;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.repository.impl.BookRepositoryImpl;
import org.example.servlet.dto.BookDTO;
import org.example.servlet.mapper.BookMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final BookRepositoryImpl bookRepository;
    private final AuthorRepositoryImpl authorRepository;

    public BookService(BookRepositoryImpl bookRepository, AuthorRepositoryImpl authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public BookDTO getBookById(Integer id) {
        Book book = bookRepository.findById(id);
        if (book != null) {
            BookDTO bookDTO = BookMapper.toDTO(book);
            bookDTO.setRelatedBookIds(book.getRelatedBooks().stream()
                    .map(Book::getId)
                    .collect(Collectors.toList()));
            return bookDTO;
        }
        return null;
    }

    public void createBook(BookDTO bookDTO) {
        if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        if (bookDTO.getAuthorId() <= 0) {
            throw new IllegalArgumentException("Invalid author ID: " + bookDTO.getAuthorId());
        }
        Author author = authorRepository.findById(bookDTO.getAuthorId());
        if (author == null) {
            throw new IllegalArgumentException("Author not found for ID: " + bookDTO.getAuthorId());
        }

        List<Book> relatedBooks = new ArrayList<>();
        for (Integer relatedId : bookDTO.getRelatedBookIds()) {
            Book relatedBook = bookRepository.findById(relatedId);
            if (relatedBook != null) {
                relatedBooks.add(relatedBook);
            }
        }
        Book book = BookMapper.toEntity(bookDTO, author, relatedBooks);
        author.addBook(book);
        bookRepository.save(book);

        for (Book relatedBook : relatedBooks) {
            relatedBook.addRelatedBook(book);
            bookRepository.saveRelatedBooks(relatedBook);
        }
        bookRepository.saveRelatedBooks(book);

    }

    public void updateBook(int id, BookDTO bookDTO) {
        Author author = authorRepository.findById(bookDTO.getAuthorId());
        if (author == null) {
            throw new IllegalArgumentException("Author not found for ID: " + bookDTO.getAuthorId());
        }
        Book book = bookRepository.findById(id);
        if (book != null) {
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(author);

            book.getRelatedBooks().clear();

            List<Book> relatedBooks = new ArrayList<>();
            for (Integer relatedBookId : bookDTO.getRelatedBookIds()) {
                Book relatedBook = bookRepository.findById(relatedBookId);
                if (relatedBook != null) {
                    relatedBooks.add(relatedBook);
                    book.addRelatedBook(relatedBook);
                    bookRepository.saveRelatedBooks(relatedBook);
                }
            }
            book.setRelatedBooks(relatedBooks);
            bookRepository.update(book);
        }
    }

    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }
}