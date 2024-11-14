package com.backend.management.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.awt.*;
import java.util.List;

@Getter
@Setter
public class PaginatedResponse<T> {
    private List<T> data;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PaginatedResponse<T> of(Page<T> page) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.setData(page.getContent());
        response.setCurrentPage(page.getNumber());
        response.setTotalItems(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setSize(page.getSize());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
