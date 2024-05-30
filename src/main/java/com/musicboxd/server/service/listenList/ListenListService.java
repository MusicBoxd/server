package com.musicboxd.server.service.listenList;

import org.springframework.http.ResponseEntity;

public interface ListenListService {

    boolean addToListenList(String uri);

    boolean removeFromListenList(String uri);

    ResponseEntity<?> getUserListenList();
}
