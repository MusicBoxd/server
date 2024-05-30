package com.musicboxd.server.controller;

import com.musicboxd.server.dto.*;
import com.musicboxd.server.model.AuthenticationRequest;
import com.musicboxd.server.model.AuthenticationResponse;
import com.musicboxd.server.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("user/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignUpRequest signUpRequest){
        UserDTO userDTO = authService.createUser(signUpRequest);
        if(userDTO == null){
            return new ResponseEntity<>("User Not Created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authService.signin(authenticationRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        AuthenticationResponse response = authService.refreshToken(refreshTokenRequest);
        if (response == null) {
            return new ResponseEntity<>("Invalid Refresh Token", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("admin/signup")
    public ResponseEntity<?> signupAdmin(@RequestBody SignUpRequest signUpRequest){
        UserDTO adminDTO = authService.createAdmin(signUpRequest);
        if(adminDTO == null){
            return new ResponseEntity<>("Admin Not Created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(adminDTO, HttpStatus.CREATED);
    }

}
