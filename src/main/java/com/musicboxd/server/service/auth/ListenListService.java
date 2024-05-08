package com.musicboxd.server.service.auth;

public interface ListenListService {

    boolean addToListenList(String albumId);

    boolean removeFromListenList(String albumId);
}
