package com.example.bookshopwebapplication.http.response.reviews;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private int rating;
    private String ratingLabel;
    private String content;
    private String reviewDate;
    private Long userId;
    private String userName;
}
