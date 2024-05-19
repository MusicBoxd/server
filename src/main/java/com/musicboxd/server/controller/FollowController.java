package com.musicboxd.server.controller;

import com.musicboxd.server.model.User;
import com.musicboxd.server.service.follow.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class FollowController {
    @Autowired
    FollowService followService;
    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@PathVariable Long followingId){
        boolean success = followService.followUser(followingId);
        if (success) {
            return ResponseEntity.ok("followed user successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to follow user.");
    }

    @DeleteMapping("/{unfollowId}")
    public ResponseEntity<String> unfollowUser (@PathVariable Long unfollowId){
        boolean success = followService.unfollowUser(unfollowId);
        if (success) {
            return ResponseEntity.ok("Unfollowed user successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to unfollow user.");
        }
    }
    @GetMapping("/getFollowers")
    public ResponseEntity<Set<User>> getFollowers(){
        Set<User> followers = followService.getfollowers();
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/getFollowing")
    public ResponseEntity<Set<User>> getFollowing(){
        Set<User> following = followService.getfollowing();
        return ResponseEntity.ok(following);
    }
}
