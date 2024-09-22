package org.example.servlet.mapper;
import org.example.model.Author;
import org.example.model.Book;
import org.example.servlet.dto.BookDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    private BookMapper() {
    }

    public static BookDTO toDTO(Book book) {
        if (book == null) return null;

        List<Integer> relatedBookIds = book.getRelatedBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor().getId(), relatedBookIds);
    }

    public static Book toEntity(BookDTO bookDTO, Author author, List<Book> relatedBooks) {
        if (bookDTO == null || author == null) return null;

        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(author);


        List<Book> newRelatedBooks = new ArrayList<>();

        for (Book relatedBook : relatedBooks) {
            newRelatedBooks.add(relatedBook);
            relatedBook.addRelatedBook(book);
        }

        book.setRelatedBooks(newRelatedBooks);
        return book;
    }
}