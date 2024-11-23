package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "librarians")
public class Librarian extends Person {
    @Id
    private String librarianId;
    private String username;
    private String password;


    public boolean matchPassword(String password){
        return this.password.equals(password);
    }


    public Librarian(String name, String email, String phoneNumber, String address, String librarianId, String username, String password) {
        super(name, email, phoneNumber, address);
        this.librarianId = librarianId;
        this.username = username;
        this.password = password;
    }

    public String getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(String librarianId) {
        this.librarianId = librarianId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
