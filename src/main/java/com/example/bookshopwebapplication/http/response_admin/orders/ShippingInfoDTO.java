package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInfoDTO {
    private Long id;
    private String receiverName;
    private String receiverEmail;
    private String receiverPhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String ward;
    private String postalCode;
    private String fullAddress;
    private String shippingNotes;
    private String trackingNumber;
    private String shippingCarrier;

    public ShippingInfoDTO(ResultSet rs) throws SQLException {
        this.setReceiverName(rs.getString("receiver_name"));
        this.setReceiverEmail(rs.getString("receiver_email"));
        this.setReceiverPhone(rs.getString("receiver_phone"));
        this.setAddressLine1(rs.getString("address_line1"));
        this.setAddressLine2(rs.getString("address_line2"));
        this.setCity(rs.getString("city"));
        this.setDistrict(rs.getString("district"));
        this.setWard(rs.getString("ward"));
        this.setPostalCode(rs.getString("postal_code"));
        this.setTrackingNumber(rs.getString("tracking_number"));
        this.setShippingCarrier(rs.getString("shipping_carrier"));

        // Tạo địa chỉ đầy đủ
        String fullAddress = rs.getString("address_line1");
        String addressLine2 = rs.getString("address_line2");
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            fullAddress += ", " + addressLine2;
        }
        fullAddress += ", " + rs.getString("ward");
        fullAddress += ", " + rs.getString("district");
        fullAddress += ", " + rs.getString("city");

        this.setFullAddress(fullAddress);
    }

}
