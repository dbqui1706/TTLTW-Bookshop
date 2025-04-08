package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineDTO {
    private String status;
    private String label;
    private String icon;
    private Timestamp timestamp;
    private String formattedDate;
    private boolean completed;
    private boolean active;
    private boolean estimated;
}