package com.musicboxd.server.service.auth;

import com.musicboxd.server.dto.SignUpRequest;
import com.musicboxd.server.dto.UpdateUserRequest;
import com.musicboxd.server.dto.UserDTO;
import com.musicboxd.server.enums.UserRole;
import com.musicboxd.server.model.AuthenticationRequest;
import com.musicboxd.server.model.AuthenticationResponse;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.auth.AuthService;
import com.musicboxd.server.service.auth.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    @Autowired
    UserRepository userRepo;

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    @Override
    public UserDTO createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.USER);

        User createdUser = userRepo.save(user);

        return convertToDto(createdUser);
    }
    @Override
    public UserDTO createAdmin(SignUpRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.ADMIN);

        User createdAdmin = userRepo.save(user);

        return convertToDto(createdAdmin);
    }
    public AuthenticationResponse signin(AuthenticationRequest signInRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername()
                ,signInRequest.getPassword()));
        var user = userRepo.findByUsername(signInRequest.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("Invalid Username"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(),user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setRefreshJwt(refreshToken);
        authenticationResponse.setUserId(user.getId());
        authenticationResponse.setUserRole(user.getUserRole());
        return authenticationResponse;

    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepo.findById(userId).orElse(null);
        if(user==null)
            return null;
        BeanUtils.copyProperties(updateUserRequest,user);
        userRepo.save(user);

        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}