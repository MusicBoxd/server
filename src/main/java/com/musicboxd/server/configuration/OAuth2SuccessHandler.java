package com.musicboxd.server.configuration;

import com.musicboxd.server.enums.UserRole;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.jwt.JWTService;
import com.musicboxd.server.service.jwt.UserService;
import jakarta.servlet.ServletException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JWTService jwtService, UserService userService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String username = oauthUser.getAttribute("email");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setName(oauthUser.getAttribute("name"));
            user.setUserRole(UserRole.USER);
            user.setPassword("default-password");

            userRepository.save(user);
        }

        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);
        String jwtToken = jwtService.generateToken(userDetails);

        System.out.println(jwtToken);

        response.setHeader("Authorization", "Bearer " + jwtToken);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
