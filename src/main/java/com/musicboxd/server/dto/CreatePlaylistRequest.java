package com.musicboxd.server.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreatePlaylistRequest {
    private String name;
    private String description;
    private Set<String> uris;
    private boolean publicAccess;
}
