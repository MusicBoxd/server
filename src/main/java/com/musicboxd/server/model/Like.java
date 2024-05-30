package com.musicboxd.server.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uris;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
