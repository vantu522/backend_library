package com.backend.management.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message); // Truyền thông báo lỗi vào constructor của RuntimeException
    }
}
