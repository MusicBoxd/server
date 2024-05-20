package com.musicboxd.server.service.follow;

import com.musicboxd.server.model.Follow;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.FollowRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class FollowServiceImp implements FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Transactional
    @Override
    public boolean followUser(Long followingId) {
        User follower = retriveLoggedInUser();
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        if (follower == null || following == null) {
            return false;
        }
        if (follower.equals(following)) {
            return false;
        }
        if (isFollowing(follower, following)) {
            return false;
        }
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(following);
        followRepository.save(follow);

        follower.setFollowingCount(following.getFollowerCount() + 1);
        following.setFollowerCount(follower.getFollowingCount() + 1);
        userRepository.save(follower);
        userRepository.save(following);
        return true;
    }
    private boolean isFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }
    @Transactional
    @Override
    public boolean unfollowUser(Long unfollowId) {
        User loggedInUser = retriveLoggedInUser();
        User unfollowUser = userRepository.findById(unfollowId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        if (loggedInUser == null || unfollowUser == null){
            return false;
        }
        if (!isFollowing(loggedInUser, unfollowUser)) {
            return false;
        }
        if (loggedInUser.equals(unfollowUser)){
            return false;
        }
        followRepository.deleteByFollowerAndFollowed(loggedInUser, unfollowUser);


        loggedInUser.setFollowingCount(loggedInUser.getFollowingCount() - 1);
        unfollowUser.setFollowerCount(unfollowUser.getFollowerCount() - 1);
        userRepository.save(loggedInUser);
        userRepository.save(unfollowUser);
        return true;
    }
    @Override
    public ResponseEntity<Integer> getFollowingCount() {
        User user = retriveLoggedInUser();
        int following = followRepository.countByFollowerId(user);
        return ResponseEntity.ok(following);
    }
    @Override
    public ResponseEntity<Integer> getFollowersCount() {
        User user = retriveLoggedInUser();
        int followers = followRepository.countByFollowedId(user);
        return ResponseEntity.ok(followers);
    }
    @Override
    public ResponseEntity<Set<User>> getfollowers() {
        User user = retriveLoggedInUser();
        if (user == null) {
            return new ResponseEntity<>(Collections.emptySet(), HttpStatus.EXPECTATION_FAILED);
        }
        Set<User> followers = followRepository.findFollowersByFollowed(user);
        return ResponseEntity.ok(followers);
    }
    @Override
    public ResponseEntity<Set<User>> getfollowing() {
        User user = retriveLoggedInUser();
        if (user==null) {
            return new ResponseEntity<>(Collections.emptySet(), HttpStatus.EXPECTATION_FAILED);
        }
        Set<User> following = followRepository.findFollowedByFollowers(user);
        return ResponseEntity.ok(following);
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
