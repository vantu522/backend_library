package com.backend.management.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
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
    private String img;
    private String nxb;
    private List<String> likedByMembers;
    private Integer pageCount;
    @CreatedDate
    private Date createdDate;

    public Book() {
    }

    public Book(
            String bookId,
            String title,
            String description,
            List<String> author,
            Integer publicationYear,
            List<BookCategory> bigCategory,
            Boolean availability,
            Integer quantity,
            String img,
            String nxb,
            List<String> likedByMembers,
            Integer pageCount
    ) { this.bookId = bookId;
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

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<BookCategory> getBigCategory() {
        return bigCategory;
    }

    public void setBigCategory(List<BookCategory> bigCategory) {
        this.bigCategory = bigCategory;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNxb() {
        return nxb;
    }

    public void setNxb(String nxb) {
        this.nxb = nxb;
    }

    public List<String> getLikedByMembers() {
        return likedByMembers;
    }

    public void setLikedByMembers(List<String> likedByMembers) {
        this.likedByMembers = likedByMembers;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}