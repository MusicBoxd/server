package com.musicboxd.server.service.follow;

import com.musicboxd.server.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface FollowService {
    boolean followUser(Long followingId);

    ResponseEntity<Set<User>> getfollowers();

    ResponseEntity<Set<User>> getfollowing();

    boolean unfollowUser(Long unfollowId);

    ResponseEntity<Integer> getFollowingCount();

    ResponseEntity<Integer> getFollowersCount();
}
