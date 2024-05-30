package com.musicboxd.server.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewTitle;

    @Column(nullable = false)
    private String uris;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private float rating;
}
