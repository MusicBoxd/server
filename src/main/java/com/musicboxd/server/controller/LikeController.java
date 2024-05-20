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

    @PostMapping("/add/{albumId}")
    public ResponseEntity<String> addLike(@PathVariable String albumId){
        return likeService.addLike(albumId);
    }

    @DeleteMapping("/remove/{albumId}")
    public ResponseEntity<String> removeLike(@PathVariable String albumId){
        return likeService.removeLike(albumId);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Like>> getLikesByUser(){
        return likeService.getLikesByUser();
    }
}
