package com.musicboxd.server.repository;

import com.musicboxd.server.model.Follow;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    List<Follow> findByFollowerId(Long followerId);

    List<Follow> findByFollowedId(Long followingId);
    boolean existsByFollowerAndFollowed(User follower, User followed);
    @Query("SELECT f.follower FROM Follow f WHERE f.followed = :user")
    Set<User> findFollowersByFollowed(Optional<User> user);

    @Query("SELECT f.followed FROM Follow f WHERE f.follower = :user")
    Set<User> findFollowedByFollowers(Optional<User> user);

    void deleteByFollowerAndFollowed(User follower, User followed);
}
