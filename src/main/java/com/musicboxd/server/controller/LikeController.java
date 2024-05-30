package com.musicboxd.server.controller;

import com.musicboxd.server.model.Like;
import com.musicboxd.server.service.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/user/like")
public class LikeController {

    @Autowired
    LikeService likeService;

    @PostMapping("/add/{uris}")
    public ResponseEntity<String> addLike(@PathVariable String uris){
        return likeService.addLike(uris);
    }

    @DeleteMapping("/remove/{uris}")
    public ResponseEntity<String> removeLike(@PathVariable String uris){
        return likeService.removeLike(uris);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Like>> getLikesByUser(){
        return likeService.getLikesByUser();
    }
}
