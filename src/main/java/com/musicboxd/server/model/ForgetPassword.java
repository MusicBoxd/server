package com.musicboxd.server.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ForgetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Date expirationTime;

    @OneToOne
    private User user;
}
