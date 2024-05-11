package com.musicboxd.server.dto;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String username;
    private String profilePic;
    private String headerPic;
}

