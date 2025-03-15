package com.example.bookshopwebapplication.http.response.product;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class ProductTable {
    // Enum cho trạng thái sản phẩm
    public enum StockProduct {
        // Còn hàng, sắp hết hàng, hết hàng
       DEFAULT, AVAILABLE, ALMOST_OUT_OF_STOCK, OUT_OF_STOCK
    }
    public enum SortOption {
        DEFAULT,
        PRICE_ASC,
        PRICE_DESC,
        NAME_ASC,
        NAME_DESC,
        POPULARITY_ASC,
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    private Long id;
    private String imageName;
    private String name;
    private String author;
    private String publisher;
    private String publishYear;
    private String category;
    private double salePrice;
    private double basePrice;
    private double discount;
    private int sold;
    private int inventory;
    private StockProduct status;
    private String description;
    private int pageSize;
    private boolean isTransaction;
    private Timestamp saleStartDate;
    private Timestamp saleEndDate;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
