package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Double subtotal;
    private Double discount;
    private Double shipping;
    private Double tax;
    private Double total;

    public OrderSummaryDTO mapRow(ResultSet rs) throws SQLException {
        return OrderSummaryDTO.builder()
                .subtotal(rs.getDouble("subtotal"))
                .discount(rs.getDouble("discount_amount"))
                .shipping(rs.getDouble("delivery_price"))
                .tax(rs.getDouble("tax_amount"))
                .total(rs.getDouble("total_amount"))
                .build();
    }
}