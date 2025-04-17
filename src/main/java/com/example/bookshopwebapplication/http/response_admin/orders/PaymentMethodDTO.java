package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean requiresConfirmation;
    private Double processingFee;

    public PaymentMethodDTO(ResultSet rs) throws SQLException {
        this.setId(rs.getLong("id"));
        this.setName(rs.getString("name"));
        this.setCode(rs.getString("code"));
        this.setDescription(rs.getString("description"));
        this.setIcon(rs.getString("icon"));
    }
}