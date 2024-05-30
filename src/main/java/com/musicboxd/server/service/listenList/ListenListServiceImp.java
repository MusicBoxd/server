package com.musicboxd.server.service.listenList;

import com.musicboxd.server.model.ListenList;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.ListenListRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListenListServiceImp implements ListenListService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ListenListRepository listenListRepo;
    @Transactional
    @Override
    public boolean addToListenList(String uri) {
        User loginedUser = retriveLoggedInUser();
        if (loginedUser == null){
            return false;
        }
        if (addedToList(uri, loginedUser)){
            return false;
        }
        ListenList listenList = new ListenList();
        listenList.setUser(loginedUser);
        listenList.setUri(uri);
        listenListRepo.save(listenList);
        return true;
    }

    private boolean addedToList(String uri, User loginedUser) {
        return listenListRepo.existsByUriAndUser(uri,loginedUser);
    }

    @Transactional
    @Override
    public boolean removeFromListenList(String uri) {
        User loginedUser = retriveLoggedInUser();
        if (loginedUser == null){
            return false;
        }
        if (!addedToList(uri, loginedUser)){
            return false;
        }
        listenListRepo.deleteByUriAndUser(uri, loginedUser);
        return true;
    }

    @Override
    public ResponseEntity<?> getUserListenList() {
        User loggedInUser = retriveLoggedInUser();
        List<ListenList> UserListenList = listenListRepo.findByUserId(loggedInUser.getId());
        List<String> uris = new ArrayList<>();
        for (ListenList listenList : UserListenList){
            uris.add(listenList.getUri());
        }
        return ResponseEntity.ok(uris);
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
