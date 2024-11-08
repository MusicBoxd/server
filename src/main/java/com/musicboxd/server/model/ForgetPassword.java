package com.musicboxd.server.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ForgetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @Column(nullable = false)
    private String otp;

    private LocalDateTime expirationTime;

    @OneToOne
    private User user;
}
