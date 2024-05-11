package com.musicboxd.server.service.listenList;

public interface ListenListService {

    boolean addToListenList(String albumId);

    boolean removeFromListenList(String albumId);
}
