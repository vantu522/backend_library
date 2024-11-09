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
    private Integer quatity;
    private Boolean availability;
    private String img;
    private String nxb;




    public Book() {
    }

    public Book(String idBook, String name, String description, List<String> author, Integer publicationYear, List<BookCategory> bigCategory , Integer quatity, Boolean availability, String img,String nxb) {
        this.idBook = idBook;
        this.name = name;
        this.description = description;
        this.author = new ArrayList<>(author != null ? author : new ArrayList<>());
        this.publicationYear = publicationYear;
        this.bigCategory = new ArrayList<>(bigCategory != null ? bigCategory : new ArrayList<>());
        this.quatity = quatity;
        this.availability = availability;
        this.img=img;
        this.nxb=nxb;
    }
    public String getIdBook() {
        return idBook;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthor() {
        return author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public List<BookCategory> getCategory() {
        return bigCategory;
    }

    public Integer getQuality() {
        return quatity;
    }

    public String getNxb() {
        return nxb;
    }

    public String getImg() {
        return img;
    }

    public Boolean getAvailability() {
        return availability;
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

    public void setQuality(Integer quatity) {
        this.quatity = quatity;
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