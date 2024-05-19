package com.musicboxd.server.repository;

import com.musicboxd.server.dto.ListenListDTO;
import com.musicboxd.server.model.ListenList;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListenListRepository extends JpaRepository<ListenList, String> {
    void deleteByAlbumIdAndUser(String albumId, User user);

    boolean existsByAlbumIdAndUser(String albumId, User loginedUser);

    List<ListenList> findByUserId(Long id);
}
