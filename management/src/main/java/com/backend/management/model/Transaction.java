package com.backend.management.model;
import java.time.LocalDate;

public class Transaction {
    private Book book;
    private LocalDate dueDate;

    public Transaction(Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.book = book;
        this.dueDate = dueDate;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }


}