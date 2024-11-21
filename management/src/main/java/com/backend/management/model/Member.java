package com.backend.management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document(collection = "members")
public class Member extends Person {
    @Id
    private String memberId;

    private int booksBorrowed;

    public Member(String name, String email, String phoneNumber, String address, String memberId,  int booksBorrowed) {
        super(name, email, phoneNumber, address);
        this.memberId = memberId;
        this.booksBorrowed = booksBorrowed;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }



    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }
}
