package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "boooks")
public class Book {
    @Id
    private String idBook;

    private String name;
    private String description;
    private List<String> bigCategory;
    private List<String> smallCategory;
    private List<String> author;
    private String NXB;
    private String img;
}
