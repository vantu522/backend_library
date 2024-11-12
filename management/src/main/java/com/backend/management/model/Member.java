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
    private List<String> transactions;
    private int booksBorrowed;

    public Member(String name, String email, String phoneNumber, String address, String memberId, List<String> transactions, int booksBorrowed) {
        super(name, email, phoneNumber, address);
        this.memberId = memberId;
        this.transactions = transactions;
        this.booksBorrowed = booksBorrowed;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }
}
