package com.backend.management.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message); // Truyền thông báo lỗi vào constructor của RuntimeException
    }
}
