package com.backend.management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Document(collection = "members")
public class Member extends Person {
    @Id
    private String memberId;
    private int booksBorrowed;
//    private LocalDateTime createdDate;
    private String username;
    private String password;

    public Member(String name, String email, String phoneNumber, String address, String memberId,  int booksBorrowed, String username, String password) {
        super(name, email, phoneNumber, address);
        this.memberId = memberId;
        this.booksBorrowed = booksBorrowed;
//        this.createdDate=createdDate;
        this.username = username;
        this.password = password;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }


//    public LocalDateTime getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(LocalDateTime createdDate) {
//        this.createdDate = createdDate;
//    }

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }


    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return  password;
    }

}
