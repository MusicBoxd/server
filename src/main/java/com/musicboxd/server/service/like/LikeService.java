package com.musicboxd.server.service.like;

import com.musicboxd.server.model.Like;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikeService {
    ResponseEntity<String> addLike(String uris);

    ResponseEntity<String> removeLike(String uris);

    ResponseEntity<List<Like>> getLikesByAlbum(String uris);

    ResponseEntity<List<Like>> getLikesByUser();
}
