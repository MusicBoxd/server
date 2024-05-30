package com.musicboxd.server.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String uris;
    private String reviewText;
    private String reviewTitle;
    private float rating;
}
