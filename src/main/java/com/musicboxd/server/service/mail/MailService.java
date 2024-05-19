package com.musicboxd.server.service.mail;

import com.musicboxd.server.dto.ChangePassword;
import com.musicboxd.server.dto.MailRequest;
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

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
public class MailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTService jwtService;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendMail(MailRequest mailRequest) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setSubject(mailRequest.getSubject());
        simpleMailMessage.setText(mailRequest.getBody());
        simpleMailMessage.setTo(mailRequest.getEmail());

        javaMailSender.send(simpleMailMessage);
        ResponseEntity.ok("Mail Send");
    }

    public ResponseEntity<?> sendOtp(User user) {
        String otp = otpGenerator();
        MailRequest mailRequest = new MailRequest();
        mailRequest.setEmail(user.getUsername());
        mailRequest.setSubject("Your OTP for Forget Password");
        mailRequest.setBody("This the OTP for Forget Password : "+ otp);

        ForgetPassword forgetPassword = new ForgetPassword();
        forgetPassword.setOtp(otp);
        forgetPassword.setExpirationTime(new Date(System.currentTimeMillis() + 80 * 1000));
        forgetPassword.setUser(user);

        sendMail(mailRequest);
        forgetPasswordRepository.save(forgetPassword);
        return new ResponseEntity<>("Mail sent for Verification", HttpStatus.OK);

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


    public ResponseEntity<?> verifyOTP(User user, String otp) {
        ForgetPassword forgetPassword = forgetPasswordRepository.findByOtpAndUser(otp,user);
        if (forgetPassword == null) {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
        if(forgetPassword.getExpirationTime().before(Date.from(Instant.now()))){
            forgetPasswordRepository.deleteById(forgetPassword.getFid());
            return new ResponseEntity<>("OTP Expired !!!",HttpStatus.EXPECTATION_FAILED);
        }

        if (jwtService != null) {
            String token = jwtService.generateToken(user);
            forgetPasswordRepository.deleteById(forgetPassword.getFid());

            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("JWTService not available", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> changePassword(ChangePassword changePassword) {
        User user = retriveUser();
        if(!Objects.equals(changePassword.getPassword(),changePassword.getRepeatPassword())){
            return new ResponseEntity<>("Please Enter the Password again", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = new BCryptPasswordEncoder().encode(changePassword.getPassword());
        userRepository.updatePassword(user.getUsername(), encodedPassword);
        return ResponseEntity.ok("Password Updated");
    }

    private User retriveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated())
            throw new BadCredentialsException("Bad Credentials login ");
        String username = authentication.getName();
        System.out.println("In Logged In User "+username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found "));
    }
}
