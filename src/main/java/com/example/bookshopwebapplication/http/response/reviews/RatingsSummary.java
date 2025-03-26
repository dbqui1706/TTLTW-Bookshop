package com.example.bookshopwebapplication.http.response.reviews;

import lombok.Data;

import java.util.Map;

@Data
public class RatingsSummary {
    private double averageRating;
    private int totalReviews;
    private Map<Integer, Integer> distribution; // Số lượng đánh giá theo thang điểm
    private Map<Integer, Integer> percentages; // Phần trăm đánh giá theo thang điểm
}
