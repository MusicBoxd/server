package com.musicboxd.server.controller;

import com.musicboxd.server.model.User;
import com.musicboxd.server.service.auth.FollowService;
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
        followService.followUser(followingId);
        return ResponseEntity.ok("Success Followed ");
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
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello USer");
    }
}
