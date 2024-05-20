package com.musicboxd.server.service.like;

import com.musicboxd.server.model.Like;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikeService {
    ResponseEntity<String> addLike(String albumId);

    ResponseEntity<String> removeLike(String albumId);

    ResponseEntity<List<Like>> getLikesByAlbum(String albumId);

    ResponseEntity<List<Like>> getLikesByUser();
}
