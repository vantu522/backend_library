package com.backend.management.model;


import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "books")
public class Book {

    private String id;
    private String name;
    private String author;
    private String category;
    private int quanlity;
    private boolean availability;

}
