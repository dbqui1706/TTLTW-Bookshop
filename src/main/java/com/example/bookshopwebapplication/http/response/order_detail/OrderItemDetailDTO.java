package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDetailDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private String author;
    private String variant;
    private Double basePrice;
    private Double discountPercent;
    private Double price;
    private Integer quantity;
    private Double subtotal;

}
