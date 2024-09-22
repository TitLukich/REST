package org.example.repository;
import org.example.model.Author;
import java.util.List;


public interface AuthorRepository extends SimpleRepository<Author, Integer>{
    @Override
    Author findById(Integer id);

    @Override
    boolean deleteById(Integer id);

    @Override
    List <Author> findAll();

    @Override
    Author save(Author author);
}
