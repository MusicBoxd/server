package com.musicboxd.server.dto;

import com.musicboxd.server.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    private String name;

    private String username;

    private String password;

    private UserRole userRole;

    private String profilePic;

    private String headerPic;
}
