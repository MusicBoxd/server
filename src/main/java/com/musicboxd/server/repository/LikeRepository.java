package com.musicboxd.server.repository;

import com.musicboxd.server.model.Like;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    void deleteByAlbumIdAndUser(String albumId, User user);

    boolean existsByAlbumIdAndUser(String albumId, User user);

    List<Like> findByAlbumId(String albumId);

    List<Like> findByUser(User user);
}
