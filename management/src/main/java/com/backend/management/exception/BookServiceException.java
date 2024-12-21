package com.backend.management.exception;

public class BookServiceException extends RuntimeException {
    public BookServiceException(String message) {
        super(message); // Truyền thông báo lỗi vào constructor của RuntimeException
    }
}
