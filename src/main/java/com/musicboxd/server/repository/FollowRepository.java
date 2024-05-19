package com.musicboxd.server.repository;

import com.musicboxd.server.model.Follow;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    @Query("SELECT f.follower FROM Follow f WHERE f.followed = :user")
    Set<User> findFollowersByFollowed(User user);

    @Query("SELECT f.followed FROM Follow f WHERE f.follower = :user")
    Set<User> findFollowedByFollowers(User user);

    void deleteByFollowerAndFollowed(User follower, User followed);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed = :user")
    int countByFollowedId(User user);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower = :user")
    int countByFollowerId(User user);
}
