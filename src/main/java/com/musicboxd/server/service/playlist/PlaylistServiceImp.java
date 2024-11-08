package com.musicboxd.server.service.playlist;

import com.musicboxd.server.dto.CreatePlaylistRequest;
import com.musicboxd.server.dto.PlaylistDTO;
import com.musicboxd.server.model.Playlist;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.PlaylistRepository;
import com.musicboxd.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImp implements PlaylistService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    @Override
    public PlaylistDTO createPlaylist(CreatePlaylistRequest createPlaylistRequest) {
        User user = retrieveLoggedInUser();
        Playlist playlist = new Playlist();
        playlist.setName(createPlaylistRequest.getName());
        playlist.setDescription(createPlaylistRequest.getDescription());
        playlist.setPublicAccess(createPlaylistRequest.isPublicAccess());
        playlist.setUser(user);
        playlist.setUris(createPlaylistRequest.getUris());
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(savedPlaylist);
    }

    @Override
    public PlaylistDTO updatePlaylist(CreatePlaylistRequest createPlaylistRequest) {
        User user = retrieveLoggedInUser();
        Playlist playlist = playlistRepository.findById(createPlaylistRequest.getId())
                .orElseThrow( () -> new BadCredentialsException("playlist not found"));
        if(!playlist.getUser().equals(user)){
            throw new BadCredentialsException("Unauthorized to update the playlist");
        }
        playlist.setName(createPlaylistRequest.getName());
        playlist.setDescription(createPlaylistRequest.getDescription());
        playlist.setPublicAccess(createPlaylistRequest.isPublicAccess());
        playlist.setUris(createPlaylistRequest.getUris());
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(updatedPlaylist);
    }

    @Transactional
    @Override
    public PlaylistDTO addUriToPlaylist(Long playlistId, Set<String> uris) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new UsernameNotFoundException("Playlist not found"));
        playlist.getUris().addAll(uris);
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(updatedPlaylist);
    }

    @Transactional
    @Override
    public boolean deletePlaylist(Long playlistId) {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (playlistOptional.isPresent()) {
            Playlist playlist = playlistOptional.get();
            if (playlist.getUser().equals(retrieveLoggedInUser())) {
                playlistRepository.delete(playlist);
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<PlaylistDTO> getPublicPlaylists() {
        Set<Playlist> playlists = playlistRepository.findByPublicAccess(true);
        return playlists.stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    @Override
    public Set<PlaylistDTO> getUserPlaylists() {
        User user = retrieveLoggedInUser();
        Set<Playlist> playlists = playlistRepository.findByUserId(user.getId());
        return playlists.stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    @Override
    public Set<PlaylistDTO> getPrivatePlaylists() {
        Set<Playlist> playlists = playlistRepository.findByPublicAccess(false);
        return playlists.stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    private PlaylistDTO mapToDTO(Playlist playlist) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setId(playlist.getId());
        playlistDTO.setName(playlist.getName());
        playlistDTO.setDescription(playlist.getDescription());
        playlistDTO.setPublicAccess(playlist.isPublicAccess());
        playlistDTO.setUris(playlist.getUris());
        return playlistDTO;
    }

    private User retrieveLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Bad Credentials login");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}
