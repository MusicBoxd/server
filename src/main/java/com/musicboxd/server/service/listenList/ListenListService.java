package com.musicboxd.server.service.listenList;

import org.springframework.http.ResponseEntity;

public interface ListenListService {

    boolean addToListenList(String albumId);

    boolean removeFromListenList(String albumId);

    ResponseEntity<?> getUserListenList();
}
