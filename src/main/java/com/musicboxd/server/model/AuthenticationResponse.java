package com.musicboxd.server.model;

import com.musicboxd.server.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private String refreshJwt;
    private UserRole userRole;
    private Long userId;
    private String username;
}
