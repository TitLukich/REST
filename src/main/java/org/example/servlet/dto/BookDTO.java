package org.example.servlet.dto;

import java.util.ArrayList;
import java.util.List;

public class BookDTO {
    private Integer id;
    private String title;
    private Integer authorId;
    private List<Integer> relatedBookIds;

    public BookDTO() {
        this.relatedBookIds = new ArrayList<>();
    }

    public BookDTO(Integer id, String title, Integer authorId, List<Integer> relatedBookIds) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.relatedBookIds = relatedBookIds != null ? relatedBookIds : new ArrayList<>();
    }

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public List<Integer> getRelatedBookIds() {
        return relatedBookIds;
    }

    public void setRelatedBookIds(List<Integer> relatedBookTitles) {
        this.relatedBookIds = relatedBookTitles;
    }
}
