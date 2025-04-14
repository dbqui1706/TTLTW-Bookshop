package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public OrderItemDetailDTO mapRow(ResultSet rs) throws SQLException {
        return OrderItemDetailDTO.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .productName(rs.getString("product_name"))
                .productImage(rs.getString("product_image"))
                .author(rs.getString("author"))
                .basePrice(rs.getDouble("base_price"))
                .discountPercent(rs.getDouble("discount_percent"))
                .price(rs.getDouble("price"))
                .quantity(rs.getInt("quantity"))
                .subtotal(rs.getDouble("subtotal"))
                .build();
    }
}
