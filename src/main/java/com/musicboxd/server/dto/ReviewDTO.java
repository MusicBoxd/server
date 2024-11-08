package com.musicboxd.server.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String uris;
    private String reviewTitle;
    private String reviewText;
    private float rating;
}
