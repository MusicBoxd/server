package com.musicboxd.server.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String albumId;
    private String reviewText;
    private float rating;
}
