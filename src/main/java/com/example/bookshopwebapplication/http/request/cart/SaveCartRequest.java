package com.example.bookshopwebapplication.http.request.cart;

import lombok.Data;

import java.util.List;
@Data
public class SaveCartRequest {
    private Integer userId;
    private List<CartItem> cartItems;


    // Inner class để đại diện cho mỗi sản phẩm trong giỏ hàng
    @Data
    public static class CartItem {
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer productPrice;
        private Integer productDiscount;
        private Integer quantity;
    }
}
