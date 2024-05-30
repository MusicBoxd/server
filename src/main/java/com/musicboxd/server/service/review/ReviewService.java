package com.musicboxd.server.service.review;

import com.musicboxd.server.dto.ReviewDTO;
import com.musicboxd.server.model.Review;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReviewService {
    ResponseEntity<List<ReviewDTO>> getReviewsByUris(String uris);

    ResponseEntity<?> addReview(ReviewDTO reviewDTO);

    ResponseEntity<List<ReviewDTO>> getReviewsByUser(Long userId);

    ResponseEntity<?> deleteReview(Long userId);

    ResponseEntity<List<ReviewDTO>> getAllReviews();
}
