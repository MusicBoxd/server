package com.musicboxd.server.service.mail;

import com.musicboxd.server.dto.ChangePassword;
import com.musicboxd.server.dto.MailRequest;
import com.musicboxd.server.model.AuthenticationResponse;
import com.musicboxd.server.model.ForgetPassword;
import com.musicboxd.server.model.User;
import com.musicboxd.server.repository.ForgetPasswordRepository;
import com.musicboxd.server.repository.UserRepository;
import com.musicboxd.server.service.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTService jwtService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ResponseEntity<String> sendMail(MailRequest mailRequest) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setSubject(mailRequest.getSubject());
            simpleMailMessage.setText(mailRequest.getBody());
            simpleMailMessage.setTo(mailRequest.getEmail());

            javaMailSender.send(simpleMailMessage);
            logger.info("Mail sent to {}", mailRequest.getEmail());
            return ResponseEntity.ok("Mail Sent Successfully");
        } catch (Exception e) {
            logger.error("Failed to send mail to {}: {}", mailRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to Send Mail");
        }
    }

    public ResponseEntity<?> sendOtp(User user) {
        String otp = otpGenerator();
        MailRequest mailRequest = new MailRequest();
        mailRequest.setEmail(user.getUsername());
        mailRequest.setSubject("Your OTP for Forget Password");
        mailRequest.setBody("This is the OTP for Forget Password: " + otp);

        ForgetPassword forgetPassword = new ForgetPassword();
        forgetPassword.setOtp(otp);
        forgetPassword.setExpirationTime(LocalDateTime.now().plusSeconds(80));
        forgetPassword.setUser(user);

        sendMail(mailRequest);
        forgetPasswordRepository.save(forgetPassword);
        return new ResponseEntity<>("Mail sent for Verification", HttpStatus.OK);
    }

    public ResponseEntity<?> verifyOTP(User user, String otp) {
        ForgetPassword forgetPassword = forgetPasswordRepository.findByOtpAndUser(otp, user);

        if (forgetPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid OTP: OTP does not match or user not found");
        }

        if (forgetPassword.getExpirationTime().isBefore(LocalDateTime.now())) {
            forgetPasswordRepository.deleteById(forgetPassword.getFid());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("OTP Expired! Please request a new OTP.");
        }

        if (jwtService != null) {
            forgetPasswordRepository.deleteById(forgetPassword.getFid());

            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            AuthenticationResponse authResponse = createAuthenticationResponse(user, jwt, refreshToken);

            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("JWT Service not available. Please try again later.");
        }
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

    private String otpGenerator() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            otp.append(characters.charAt(random.nextInt(characters.length())));
        }
        return otp.toString();
    }



    public ResponseEntity<?> changePassword(ChangePassword changePassword) {
        try {
            User user = retrieveUser();
            if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
                return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
            }
            String encodedPassword = new BCryptPasswordEncoder().encode(changePassword.getPassword());
            userRepository.updatePassword(user.getUsername(), encodedPassword);
            logger.info("Password updated for user {}", user.getUsername());
            return ResponseEntity.ok("Password Updated Successfully");
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            // These exceptions are handled by GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            logger.error("Error changing password for user {}: {}", retrieveUser().getUsername(), e.getMessage());
            return new ResponseEntity<>("An error occurred while changing password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User retrieveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new BadCredentialsException("Bad Credentials. Please log in.");

        String username = authentication.getName();
        logger.info("Retrieving user: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}
