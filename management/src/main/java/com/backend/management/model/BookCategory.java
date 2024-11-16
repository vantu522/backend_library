package com.backend.management.model;

import java.util.List;
import java.util.ArrayList;

public class  BookCategory {
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

    private String generateSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }

    public List<String> getSmallCategory() {
        return smallCategory;
    }

    public void setSmallCategory(List<String> smallCategory) {
        this.smallCategory = smallCategory;
    }
}
