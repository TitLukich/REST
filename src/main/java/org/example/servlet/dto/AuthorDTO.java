package org.example.servlet.dto;
import java.util.List;

public class AuthorDTO {
    private Integer id;
    private String name;
    private List<String> bookTitles;

    public AuthorDTO() {
    }

    public AuthorDTO(Integer id, String name, List<String> bookTitles) {
        this.id = id;
        this.name = name;
        this.bookTitles = bookTitles;
    }

    // Геттеры и сеттеры
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

    public List<String> getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(List<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
}
