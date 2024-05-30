package com.musicboxd.server.service.like;

import com.musicboxd.server.model.Like;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.LikeRepository;
import com.musicboxd.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImp implements LikeService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    LikeRepository likeRepository;
    @Override
    public ResponseEntity<String> addLike(String uris) {
        User user = retriveLoggedInUser();
        if (user == null){
            return new ResponseEntity<>("User Not Found", HttpStatus.EXPECTATION_FAILED);
        }
        Like like = new Like();
        like.setUser(user);
        like.setUris(uris);

        likeRepository.save(like);
        return ResponseEntity.ok("Like the Album");
    }

    @Override
    public ResponseEntity<String> removeLike(String uris) {
        User user = retriveLoggedInUser();
        if (user == null){
            return new ResponseEntity<>("User Not Found", HttpStatus.EXPECTATION_FAILED);
        }
        if(!likedAlbum(uris, user)){
            return new ResponseEntity<>("User Not Liked this Album ",HttpStatus.EXPECTATION_FAILED);
        }
        likeRepository.deleteByUrisAndUser(uris, user);
        return ResponseEntity.ok("Removed Like");
    }

    @Override
    public ResponseEntity<List<Like>> getLikesByAlbum(String uris) {
        return ResponseEntity.ok(likeRepository.findByUris(uris));
    }

    @Override
    public ResponseEntity<List<Like>> getLikesByUser() {
        return ResponseEntity.ok(likeRepository.findByUser(retriveLoggedInUser()));
    }

    private boolean likedAlbum(String uris, User user) {
        return likeRepository.existsByUrisAndUser(uris, user);
    }

    private User retriveLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated())
            throw new BadCredentialsException("Bad Credentials login ");
        String username = authentication.getName();
        System.out.println("In Logged In User "+username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found "));
    }
}
