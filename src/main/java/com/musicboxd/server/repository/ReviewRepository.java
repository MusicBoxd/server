package com.musicboxd.server.repository;

import com.musicboxd.server.dto.ReviewDTO;
import com.musicboxd.server.model.Review;
import com.musicboxd.server.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.uris = :uris")
    List<Review> findByUris(@Param("uris") String uris);

    List<Review> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);

    boolean existsByUrisAndUser(String uris, User user);
}
