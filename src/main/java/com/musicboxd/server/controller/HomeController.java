package com.musicboxd.server.controller;

import com.musicboxd.server.dto.ReviewDTO;
import com.musicboxd.server.model.Like;
import com.musicboxd.server.model.User;
import com.musicboxd.server.service.like.LikeService;
import com.musicboxd.server.service.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    ReviewService reviewService;
    @Autowired
    LikeService likeService;
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews(){
        return reviewService.getAllReviews();
    }
    @GetMapping("review/{albumId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByAlbumId(@PathVariable String albumId){
        return reviewService.getReviewsByAlbumId(albumId);
    }
    @GetMapping("review/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId){
        return reviewService.getReviewsByUser(userId);
    }
    @GetMapping("like/album/{albumId}")
    public ResponseEntity<List<Like>> getLikesByAlbum(@PathVariable String albumId){
        return likeService.getLikesByAlbum(albumId);
    }
}
