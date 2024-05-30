package com.musicboxd.server.repository;

import com.musicboxd.server.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Set<Playlist> findByUserId(Long userId);
    Set<Playlist> findByPublicAccess(boolean publicAccess);
}

