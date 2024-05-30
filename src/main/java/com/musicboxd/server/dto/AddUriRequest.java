package com.musicboxd.server.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AddUriRequest {
    private Set<String> uris;
}
