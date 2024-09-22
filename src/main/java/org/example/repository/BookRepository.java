package org.example.repository;
import org.example.model.Book;
import java.util.List;


public interface BookRepository extends SimpleRepository <Book, Integer> {
    @Override
    Book findById(Integer id);

    @Override
    boolean deleteById(Integer id);

    @Override
    List<Book> findAll();

    @Override
    Book save(Book book);
}
