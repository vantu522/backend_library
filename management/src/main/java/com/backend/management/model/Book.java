package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class Book {
    @Id
    private String idBook;
    private String name;
    private String description;
    private String author;
    private Integer publicationYear;
    private Category category;
    private Integer quality;
    private Boolean availability;

    public Book() {
    }

    public Book(String idBook, String name, String description, String author, Integer publicationYear, Category category, Integer quality, Boolean availability) {
        this.idBook = idBook;
        this.name = name;
        this.description = description;
        this.author = author;
        this.publicationYear = publicationYear;
        this.category = category;
        this.quality = quality;
        this.availability = availability;
    }

    public String getIdBook() {
        return idBook;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
}
