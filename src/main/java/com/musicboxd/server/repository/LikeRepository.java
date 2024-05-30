package com.musicboxd.server.repository;

import com.musicboxd.server.model.Like;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    void deleteByUrisAndUser(String uris, User user);

    boolean existsByUrisAndUser(String uris, User user);

    List<Like> findByUris(String uris);

    List<Like> findByUser(User user);
}
