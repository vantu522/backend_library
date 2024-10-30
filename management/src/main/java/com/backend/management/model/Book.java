package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.ArrayList;
@Document(collection = "books")
public class Book {
    @Id
    private String idBook;
    private String name;
    private String description;
    private List<String> author;
    private Integer publicationYear;
    private List<BookCategory> category;
    private Integer quality;
    private Boolean availability;
    @Field("img")
    private String imageUrl;
    private String nxb;

    public static class BookCategory {
        private String name;
        private List<String> smallCategory;

        public BookCategory() {
            this.smallCategory = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getSmallCategory() {
            return smallCategory;
        }

        public void setSmallCategory(List<String> smallCategory) {
            this.smallCategory = smallCategory != null ? smallCategory : new ArrayList<>();
        }
    }




    public Book() {
    }

    public Book(String idBook, String name, String description, List<String> author, Integer publicationYear, List<Category> category , Integer quality, Boolean availability, String imageUrl,String nxb) {
        this.idBook = idBook;
        this.name = name;
        this.description = description;
        this.author = new ArrayList<>();
        this.publicationYear = publicationYear;
        this.category = new ArrayList<>();
        this.quality = quality;
        this.availability = availability;
        this.imageUrl=imageUrl;
        this.nxb=nxb;
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

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<BookCategory> getCategory() {
        return category;
    }

    public void setCategory(List<BookCategory> category) {
        this.category = category;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNxb() {
        return nxb;
    }

    public void setNxb(String nxb) {
        this.nxb = nxb;
    }

    public String getImg() {
        return imageUrl;
    }

    public void setImg(String imageUrlg) {
        this.imageUrl = imageUrlg;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
}