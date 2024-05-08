package com.musicboxd.server.service.auth;

import com.musicboxd.server.model.User;

import java.util.Set;

public interface FollowService {
    boolean followUser(Long followingId);

    Set<User> getfollowers();

    Set<User> getfollowing();
}