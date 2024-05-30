package com.musicboxd.server.dto;
import com.musicboxd.server.model.Address;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String username;
    private String profilePic;
    private String headerPic;
    private Address address;
}

