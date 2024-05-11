package com.musicboxd.server.service.listenList;

import com.musicboxd.server.model.ListenList;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.ListenListRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListenListServiceImp implements ListenListService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ListenListRepository listenListRepo;
    @Transactional
    @Override
    public boolean addToListenList(String albumId) {
        User loginedUser = retriveLoggedInUser();
        if (loginedUser == null){
            return false;
        }
        if (addedToList(albumId, loginedUser)){
            return false;
        }
        ListenList listenList = new ListenList();
        listenList.setUser(loginedUser);
        listenList.setAlbumId(albumId);
        listenListRepo.save(listenList);
        return true;
    }

    private boolean addedToList(String albumId, User loginedUser) {
        return listenListRepo.existsByAlbumIdAndUser(albumId,loginedUser);
    }

    @Transactional
    @Override
    public boolean removeFromListenList(String albumId) {
        User loginedUser = retriveLoggedInUser();
        if (loginedUser == null){
            return false;
        }
        if (!addedToList(albumId, loginedUser)){
            return false;
        }
        listenListRepo.deleteByAlbumIdAndUser(albumId, loginedUser);
        return true;
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
