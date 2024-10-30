package com.backend.management.model;

import java.util.List;

public class Category {
    private String name;
    private List<Category> smallCategory;

    public Category( String name, List<Category> smallCategory) {
        this.name = name;
        this.smallCategory = smallCategory;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getSmallCategory() {
        return smallCategory;
    }

    public void setSmallCategory(List<Category> smallCategory) {
        this.smallCategory = smallCategory;
    }
}