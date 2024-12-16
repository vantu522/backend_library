package com.backend.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ratings")
public class Rating {

    @Id
    private String id;
    private String bookId;
    private String memberId;
    private String reviewText;
    private double ratingScore;
    private boolean onlyScore;

    public Rating() {}

    public Rating(String bookId, String memberId, String reviewText, double ratingScore, boolean onlyScore) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.reviewText = reviewText;
        this.ratingScore = ratingScore;
        this.onlyScore = onlyScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public boolean isOnlyScore() {
        return onlyScore;
    }

    public void setOnlyScore(boolean onlyScore) {
        this.onlyScore = onlyScore;
    }
}
