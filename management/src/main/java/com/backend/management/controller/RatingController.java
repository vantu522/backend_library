package com.backend.management.controller;

import com.backend.management.model.Rating;
import com.backend.management.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ratings")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    // Gửi đánh giá
    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(@RequestBody Rating rating) {
        // Kiểm tra nội dung nếu không bật chế độ chỉ chấm điểm
//        if (!rating.isOnlyScore() && (rating.getReviewText() == null || rating.getReviewText().trim().length() < 100)) {
//            return ResponseEntity.badRequest().body("Nội dung bài đánh giá phải có ít nhất 100 từ nếu không bật chế độ 'Chỉ chấm điểm'.");
//        }

        Rating savedRating = ratingRepository.save(rating);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đánh giá đã được gửi thành công!");
        response.put("ratingId", savedRating.getId());
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách đánh giá của một cuốn sách
    @GetMapping("/book")
    public ResponseEntity<?> getRatingsByBook(@RequestParam String bookId) {
        List<Rating> ratings = ratingRepository.findByBookId(bookId);
        return ResponseEntity.ok(ratings);
    }

    // Tính trung bình điểm của một cuốn sách
    @GetMapping("/book/average")
    public ResponseEntity<?> getAverageRatingByBook(@RequestParam String bookId) {
        List<Rating> ratings = ratingRepository.findByBookId(bookId);
        double average = ratings.stream()
                .mapToDouble(Rating::getRatingScore)
                .average()
                .orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("bookId", bookId);
        response.put("averageScore", average);
        return ResponseEntity.ok(response);
    }
}
