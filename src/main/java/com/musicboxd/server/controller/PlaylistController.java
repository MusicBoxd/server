package com.musicboxd.server.controller;

import com.musicboxd.server.dto.AddUriRequest;
import com.musicboxd.server.dto.CreatePlaylistRequest;
import com.musicboxd.server.dto.PlaylistDTO;
import com.musicboxd.server.service.playlist.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user/playlist")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/createWithSongs")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody CreatePlaylistRequest createPlaylistRequest){
        PlaylistDTO playlistDTO;
        if (createPlaylistRequest.getId() != null){
            playlistDTO = playlistService.updatePlaylist(createPlaylistRequest);
        } else {
            playlistDTO = playlistService.createPlaylist(createPlaylistRequest);
        }
        return ResponseEntity.ok(playlistDTO);
    }

    @PutMapping("/addUri/{playlistId}")
    public ResponseEntity<PlaylistDTO> addUriToPlaylist(@PathVariable Long playlistId, @RequestBody AddUriRequest addUriRequest) {
        PlaylistDTO updatedPlaylist = playlistService.addUriToPlaylist(playlistId, addUriRequest.getUris());
        return ResponseEntity.ok(updatedPlaylist);
    }

    @DeleteMapping("/deleteList/{playlistId}")
    public ResponseEntity<String > delete(@PathVariable Long playlistId){
        boolean success = playlistService.deletePlaylist(playlistId);
        if (success) {
            return ResponseEntity.ok("deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete List.");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Set<PlaylistDTO>> getUserPlaylists() {
        Set<PlaylistDTO> playlists = playlistService.getUserPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/public")
    public ResponseEntity<Set<PlaylistDTO>> getPublicPlaylists() {
        Set<PlaylistDTO> publicPlaylists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(publicPlaylists);
    }

    @GetMapping("/private")
    public ResponseEntity<Set<PlaylistDTO>> getPrivatePlaylists() {
        Set<PlaylistDTO> privatePlaylists = playlistService.getPrivatePlaylists();
        return ResponseEntity.ok(privatePlaylists);
    }

}
