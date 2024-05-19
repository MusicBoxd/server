package com.musicboxd.server.service.review;

import com.musicboxd.server.dto.ReviewDTO;
import com.musicboxd.server.model.Review;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.ReviewRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImp implements ReviewService{
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<?> addReview(Review review) {
        User user = retriveLoggedInUser();
        if (user == null){
            return new ResponseEntity<>(new UsernameNotFoundException("User Not Found"),HttpStatus.EXPECTATION_FAILED);
        }
        if (reviewed(review.getAlbumId(), user)){
            return new ResponseEntity<>("Already Reviewed",HttpStatus.EXPECTATION_FAILED);
        }
        review.setUser(user);
        Review reviewDB = reviewRepository.save(review);
        return new ResponseEntity<>(convertToDto(reviewDB), HttpStatus.OK);
    }

    private boolean reviewed(String albumId, User user) {
        return reviewRepository.existsByAlbumIdAndUser(albumId, user);
    }

    @Override
    public ResponseEntity<?> deleteReview(Long userId) {
        int deleteCount = reviewRepository.deleteByUserId(userId);
        if(deleteCount > 0){
            return ResponseEntity.ok("Successfully deleted");
        } else {
            return ResponseEntity.status(404).body("Record not found or not deleted");
        }
    }

    @Override
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews){
            reviewDTOs.add(convertToDto(review));
        }
        return ResponseEntity.ok(reviewDTOs);
    }

    @Override
    public ResponseEntity<List<ReviewDTO>> getReviewsByAlbumId(String albumId) {
        List<Review> reviews = reviewRepository.findByAlbumId(albumId);
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews){
            reviewDTOs.add(convertToDto(review));
        }
        return ResponseEntity.ok(reviewDTOs);
    }

    @Override
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews){
            reviewDTOs.add(convertToDto(review));
        }
        return new ResponseEntity<>(reviewDTOs,HttpStatus.OK);
    }

    private ReviewDTO convertToDto(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        BeanUtils.copyProperties(review, reviewDTO);
        return reviewDTO;
    }
    private User retriveLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated())
            throw new BadCredentialsException("Bad Credentials login ");
        String username = authentication.getName();
        System.out.println("In Logged In User "+username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found "));
    }
}
