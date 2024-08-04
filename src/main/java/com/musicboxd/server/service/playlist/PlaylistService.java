package com.musicboxd.server.service.playlist;

import com.musicboxd.server.dto.CreatePlaylistRequest;
import com.musicboxd.server.dto.PlaylistDTO;

import java.util.Set;

public interface PlaylistService {
    PlaylistDTO createPlaylist(CreatePlaylistRequest createPlaylistRequest);

    PlaylistDTO addUriToPlaylist(Long playlistId, Set<String> uri);

    Set<PlaylistDTO> getUserPlaylists();

    boolean deletePlaylist(Long playlistId);

    Set<PlaylistDTO> getPublicPlaylists();

    Set<PlaylistDTO> getPrivatePlaylists();

    PlaylistDTO updatePlaylist(CreatePlaylistRequest createPlaylistRequest);
}
