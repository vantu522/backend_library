package com.backend.management.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;
@Getter
@Document(collection = "books")
public class Book {
    @Id
    private String bookId;
    private String title;
    private String description;
    private List<String> author;
    private Integer publicationYear;
    private List<BookCategory> bigCategory;
    private Integer quantity;
    private Boolean availability;
    @Size(max = 5 * 1024 * 1024, message = "Image size must be less than 5MB")
    private String img;
    private String nxb;
    private List<String> likedByMembers;
    private Integer pageCount;




    public Book() {
    }


    public Book(String bookId, String title, String description, List<String> author, Integer publicationYear, List<BookCategory> bigCategory, Boolean availability, Integer quantity, String img, String nxb, List<String> likedByMembers, Integer pageCount) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.author = author;
        this.publicationYear = publicationYear;
        this.bigCategory = bigCategory;
        this.availability = availability;
        this.quantity = quantity;
        this.img = img;
        this.nxb = nxb;
        this.likedByMembers = likedByMembers;
        this.pageCount = pageCount;
    }


    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
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

    public List<BookCategory> getBigCategory() {
        return bigCategory;
    }

    public Integer getQuantity() {
        return quantity;
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
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setQuantity(Integer quatity) {
        this.quantity = quatity;
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

    public List<String> getLikedByMembers() {
        return likedByMembers;
    }

    public void setLikedByMembers(List<String> likedByMembers) {
        this.likedByMembers = likedByMembers;
    }

    public void setBigCategory(List<BookCategory> bigCategory) {
        this.bigCategory = bigCategory;
    }
}