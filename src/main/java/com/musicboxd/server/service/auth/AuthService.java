package com.musicboxd.server.service.auth;

import com.musicboxd.server.dto.RefreshTokenRequest;
import com.musicboxd.server.dto.SignUpRequest;
import com.musicboxd.server.dto.UpdateUserRequest;
import com.musicboxd.server.dto.UserDTO;
import com.musicboxd.server.model.AuthenticationRequest;
import com.musicboxd.server.model.AuthenticationResponse;

public interface AuthService {
    UserDTO createUser(SignUpRequest signUpRequest);

    UserDTO updateUser(UpdateUserRequest updateUserRequest);

    AuthenticationResponse signin(AuthenticationRequest signInRequest);

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    UserDTO createAdmin(SignUpRequest signUpRequest);
}
