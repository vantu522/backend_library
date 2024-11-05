package com.backend.management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;
@Getter
@Document(collection = "books")
public class Book {
    @Id
    private String idBook;
    private String name;
    private String description;
    private List<String> author;
    private Integer publicationYear;
    private List<BookCategory> bigCategory;
    private Integer quantity;
    private Boolean availability;
    private String img;
    private String nxb;




    public Book() {
    }

    public Book(String idBook, String name, String description, List<String> author, Integer publicationYear, List<BookCategory> bigCategory , Integer quantity, Boolean availability, String img,String nxb) {
        this.idBook = idBook;
        this.name = name;
        this.description = description;
        this.author = new ArrayList<>(author != null ? author : new ArrayList<>());
        this.publicationYear = publicationYear;
        this.bigCategory = new ArrayList<>(bigCategory != null ? bigCategory : new ArrayList<>());
        this.quantity = quantity;
        this.availability = availability;
        this.img=img;
        this.nxb=nxb;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setCategory(List<BookCategory> bigCategory) {
        this.bigCategory = bigCategory;
    }

    public void setQuality(Integer quantity) {
        this.quantity = quantity;
    }


    public void setNxb(String nxb) {
        this.nxb = nxb;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }


}