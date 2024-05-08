package com.musicboxd.server.service.auth;

import com.musicboxd.server.model.Follow;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.FollowRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class FollowServiceImp implements FollowService{
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

        return true;
    }
    private boolean isFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }
    @Transactional
    @Override
    public boolean unfollowUser(Long unfollowId) {
        User loginedUser = retriveLoggedInUser();
        User unfollowUser = userRepository.findById(unfollowId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        if (loginedUser == null || unfollowUser == null){
            return false;
        }
        if (!isFollowing(loginedUser, unfollowUser)) {
            return false;
        }
        if (loginedUser.equals(unfollowUser)){
            return false;
        }
        followRepository.deleteByFollowerAndFollowed(loginedUser, unfollowUser);
        return true;
    }
    @Override
    public Set<User> getfollowers() {
        User loginedUser = retriveLoggedInUser();
        Optional<User> user = userRepository.findByUsername(loginedUser.getUsername());
        if (user.isEmpty()) {
            return Collections.emptySet();
        }
        return followRepository.findFollowersByFollowed(user);
    }
    @Override
    public Set<User> getfollowing() {
        User loginedUser = retriveLoggedInUser();
        Optional<User> user = userRepository.findByUsername(loginedUser.getUsername());
        if (user.isEmpty()) {
            return Collections.emptySet();
        }
        return followRepository.findFollowedByFollowers(user);
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
