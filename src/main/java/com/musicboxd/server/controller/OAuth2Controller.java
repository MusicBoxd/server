package com.musicboxd.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicboxd.server.enums.UserRole;
import com.musicboxd.server.model.GoogleUser;
import com.musicboxd.server.model.User;
import com.musicboxd.server.dto.JwtResponse;
import com.musicboxd.server.dto.OAuth2Request;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.jwt.JWTService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class OAuth2Controller {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public OAuth2Controller(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/oauth2/callback")
    public ResponseEntity<?> handleGoogleOAuth2(@RequestBody OAuth2Request oAuth2Request) {
        String googleAccessToken = oAuth2Request.getAccessToken();

        GoogleUser googleUser = validateGoogleToken(googleAccessToken);

        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
        }

        User user = userRepository.findByUsername(googleUser.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(googleUser.getEmail());
            newUser.setName(googleUser.getName());
            newUser.setUserRole(UserRole.USER);
            userRepository.save(newUser);
            return newUser;
        });

        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }

    public GoogleUser validateGoogleToken(String googleAccessToken) {
        String urlString = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + googleAccessToken;
        GoogleUser googleUser = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlString);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(responseBody);

                    googleUser = new GoogleUser();
                    googleUser.setEmail(node.get("email").asText());
                    googleUser.setName(node.get("name").asText());
                    googleUser.setPicture(node.get("picture") != null ? node.get("picture").asText() : null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return googleUser;
    }

}
