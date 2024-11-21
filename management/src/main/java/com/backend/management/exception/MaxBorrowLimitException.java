package com.backend.management.exception;

public class MaxBorrowLimitException extends RuntimeException {
    public MaxBorrowLimitException(String message) {
        super(message); // Truyền thông báo lỗi vào constructor của RuntimeException
    }
}
