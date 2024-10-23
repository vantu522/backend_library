package com.backend.management.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "books")
public class Book {
    @Id
    private String id;
    private String name;
    private String author;
    private String bigCategory;
    private int quanlity;
    private boolean availability;

}
