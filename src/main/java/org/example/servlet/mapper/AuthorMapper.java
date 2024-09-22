package org.example.servlet.mapper;
import org.example.model.Author;
import org.example.servlet.dto.AuthorDTO;
import org.example.model.Book;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorMapper {

    private AuthorMapper() {
    }

    public static AuthorDTO toDTO(Author author) {
        if (author == null) return null;
        List<String> bookTitles = author.getBooks() != null ?
                author.getBooks().stream()
                        .map(Book::getTitle)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
        return new AuthorDTO(author.getId(), author.getName(), bookTitles);
    }

    public static Author toEntity(AuthorDTO authorDTO) {
        if (authorDTO == null) return null;
        Integer id = authorDTO.getId();
        return new Author(id == null ? 0 : id, authorDTO.getName());
    }
}
