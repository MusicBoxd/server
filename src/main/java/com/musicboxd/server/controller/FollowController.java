package com.musicboxd.server.controller;

import com.musicboxd.server.dto.UpdateUserRequest;
import com.musicboxd.server.dto.UserDTO;
import com.musicboxd.server.exception.UserNotFoundException;
import com.musicboxd.server.model.User;
import com.musicboxd.server.service.auth.AuthService;
import com.musicboxd.server.service.follow.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    AuthService authService;
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        UserDTO updatedUser = authService.updateUser(updateUserRequest);
        if (updatedUser == null) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@PathVariable Long followingId) {
        boolean success = followService.followUser(followingId);
        if (!success) {
            throw new UserNotFoundException("Failed to follow user with ID: " + followingId);
        }
        return ResponseEntity.ok("Followed user successfully.");
    }

    @DeleteMapping("/{unfollowId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long unfollowId) {
        boolean success = followService.unfollowUser(unfollowId);
        if (!success) {
            throw new UserNotFoundException("Failed to unfollow user with ID: " + unfollowId);
        }
        return ResponseEntity.ok("Unfollowed user successfully.");
    }

    @GetMapping("/getFollowers")
    public ResponseEntity<Set<User>> getFollowers(){
        return followService.getfollowers();
    }

    @GetMapping("/getFollowing")
    public ResponseEntity<Set<User>> getFollowing(){
        return followService.getfollowing();
    }

    @GetMapping("/getFollowingCount")
    public ResponseEntity<Integer> getFollowingCount(){
        return followService.getFollowingCount();
    }

    @GetMapping("/getFollowersCount")
    public ResponseEntity<Integer> getFollowersCount(){
        return followService.getFollowersCount();
    }
}
