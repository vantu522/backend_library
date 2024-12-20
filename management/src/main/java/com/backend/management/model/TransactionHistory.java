package com.backend.management.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transactionHistory")
public class TransactionHistory {

    // Getters và Setters
    @Getter
    @Id
    private String id;  // ID duy nhất cho mỗi giao dịch
    @Getter
    private String memberId;
    @Getter
    private String memberName;  // Tên người mượn
    @Getter
    private String bookId;
    @Setter
    @Getter
    private String title;
    private String author;
    @Setter
    @Getter
    private String phoneNumber;
    private String transactionType;
    private LocalDateTime transactionDate;
    private LocalDateTime dueDate;  // Dùng cho mượn hoặc gia hạn nếu có
    private String  status; // True là vẫn đang mượn, False là đã trả
    private String img;
    private String description;
//    private String state;

    public void setId(String id) {
        this.id = id;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {return author;}

    public void  setAuthor(String author) {
        this.author = author;
    }
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
