package com.musicboxd.server.controller;

import com.musicboxd.server.dto.ReviewDTO;
import com.musicboxd.server.model.Review;
import com.musicboxd.server.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/review")

public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO){
        return reviewService.addReview(reviewDTO);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long userId){
        return reviewService.deleteReview(userId);
    }


}
