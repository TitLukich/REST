package org.example.service.impl;

import org.example.model.Author;
import org.example.repository.impl.AuthorRepositoryImpl;
import org.example.servlet.dto.AuthorDTO;
import org.example.servlet.mapper.AuthorMapper;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorService {
    private final AuthorRepositoryImpl authorRepository;

    public AuthorService(AuthorRepositoryImpl authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorDTO getAuthorById(int id) {
        Author author = authorRepository.findById(id);
        return AuthorMapper.toDTO(author);
    }

    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void createAuthor(AuthorDTO authorDTO) {
        Author author = AuthorMapper.toEntity(authorDTO);
        authorRepository.save(author);
    }

    public void updateAuthor(int id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id);
        if (author != null) {
            author.setName(authorDTO.getName());
            authorRepository.update(author);
        }
    }

    public void deleteAuthor(int id) {
        authorRepository.deleteById(id);
    }
}
