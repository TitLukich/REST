package org.example.model;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private Integer id;
    private String title;
    private Author author;
    private List<Book> relatedBooks = new ArrayList<>();

    public Book() {
    }

    public Book(Integer id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Book> getRelatedBooks() {
        return relatedBooks;
    }

    public void setRelatedBooks(List<Book> relatedBooks) {
        this.relatedBooks = relatedBooks;
    }

    public void addRelatedBook(Book book) {
        if (!relatedBooks.contains(book)) {
            relatedBooks.add(book);
            book.addRelatedBook(this); // Устанавливаем обратную связь
        }
    }
}
