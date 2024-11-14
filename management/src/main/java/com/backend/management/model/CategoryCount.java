package com.backend.management.model;

import lombok.Data;
import org.springframework.data.mongodb.repository.Aggregation;
@Data
public class CategoryCount {
    private String id;
    private Long count;


}
