package org.example.model;
import java.util.ArrayList;
import java.util.List;


public class Author {
    private Integer id;
    private String name;
    private List<Book> books = new ArrayList<>();

    public Author() {
    }

    public Author(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            book.setAuthor(this);
        }
    }

    public void removeBook(Book book) {
        if (books.remove(book)) {
            book.setAuthor(null); // Убираем связь
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
