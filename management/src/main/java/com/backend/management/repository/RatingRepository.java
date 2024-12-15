package com.backend.management.repository;

import com.backend.management.model.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByBookId(String bookId);

    List<Rating> findByMemberId(String memberId);

    double findAverageRatingScoreByBookId(String bookId);
}
