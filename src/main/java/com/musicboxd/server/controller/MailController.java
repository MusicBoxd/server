package com.musicboxd.server.controller;

import com.musicboxd.server.dto.ChangePassword;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.mail.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forgetPassword")
public class MailController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/verify/{username}")
    public ResponseEntity<?> verifyUsername(@PathVariable String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return mailService.sendOtp(user);
    }

    @PostMapping("/verifyOTP/{otp}/{username}")
    public ResponseEntity<?> verifyOTP(@PathVariable String otp, @PathVariable String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return mailService.verifyOTP(user, otp);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword changePassword){
        return mailService.changePassword(changePassword);
    }

}
