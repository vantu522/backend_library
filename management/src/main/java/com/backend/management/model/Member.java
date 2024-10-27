package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "members")
public class Member extends Person {
    @Id
    private String memberId;
    private MembershipType Type;
    private List<Transaction> transactions;
    private int quota;

    public Member(String name, String email, String phoneNumber, String address, String memberId, MembershipType type, List<Transaction> transactions, int quota) {
        super(name, email, phoneNumber, address);
        this.memberId = memberId;
        Type = type;
        this.transactions = transactions;
        this.quota = quota;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public MembershipType getType() {
        return Type;
    }

    public void setType(MembershipType type) {
        Type = type;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }
}
