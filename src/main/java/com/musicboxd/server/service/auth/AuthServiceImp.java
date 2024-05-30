package com.musicboxd.server.service.auth;

import com.musicboxd.server.dto.RefreshTokenRequest;
import com.musicboxd.server.dto.SignUpRequest;
import com.musicboxd.server.dto.UpdateUserRequest;
import com.musicboxd.server.dto.UserDTO;
import com.musicboxd.server.enums.UserRole;
import com.musicboxd.server.model.Address;
import com.musicboxd.server.model.AuthenticationRequest;
import com.musicboxd.server.model.AuthenticationResponse;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (userRepo.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return null;
        }
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

    public AuthenticationResponse signin(AuthenticationRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        var user = userRepo.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid Username"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return createAuthenticationResponse(user, jwt, refreshToken);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.extractUsername(refreshTokenRequest.getToken());
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);
            return createAuthenticationResponse(user, jwt, refreshTokenRequest.getToken());
        }
        return null;
    }

    @Override
    public UserDTO updateUser(UpdateUserRequest updateUserRequest) {
        User user = retriveLoggedInUser();
        if (user == null)
            return null;
        user.setName(updateUserRequest.getName() != null ? updateUserRequest.getName() : user.getName());
        user.setProfilePic(updateUserRequest.getProfilePic() != null ? updateUserRequest.getProfilePic() : user.getProfilePic());
        user.setHeaderPic(updateUserRequest.getHeaderPic() != null ? updateUserRequest.getHeaderPic() : user.getHeaderPic());

        if (updateUserRequest.getAddress() != null) {
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setStreet(updateUserRequest.getAddress().getStreet());
            address.setCity(updateUserRequest.getAddress().getCity());
            address.setState(updateUserRequest.getAddress().getState());
            address.setZipCode(updateUserRequest.getAddress().getZipCode());
            address.setCountry(updateUserRequest.getAddress().getCountry());
            address.setUser(user);
            user.setAddress(address);
        }        userRepo.save(user);

        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    private User retriveLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new BadCredentialsException("Bad Credentials login ");
        String username = authentication.getName();
        System.out.println("In Logged In User " + username);
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found "));
    }

    private AuthenticationResponse createAuthenticationResponse(User user, String jwt, String refreshToken) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setRefreshJwt(refreshToken);
        authenticationResponse.setUserId(user.getId());
        authenticationResponse.setUserRole(user.getUserRole());
        authenticationResponse.setUsername(user.getUsername());

        return authenticationResponse;
    }
}
