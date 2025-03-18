package com.example.bookshopwebapplication.http.request.product;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AddRequest {
    // Thông tin cơ bản
    private String name;             // Tên sách
    private Long categoryId;      // ID thể loại
    private Double price;        // Giá gốc
    private Double discount;        // Khuyến mãi (%)
    private Integer quantity;        // Tồn kho
    private Integer totalBuy;        // Lượt mua

    // Thông tin chi tiết sách
    private String author;           // Tác giả
    private Integer pages;           // Số trang
    private String publisher;        // Nhà xuất bản
    private Integer yearPublishing;  // Năm xuất bản

    // Thời gian khuyến mãi
    private String startsAt;  // Ngày bắt đầu khuyến mãi
    private String endsAt;    // Ngày kết thúc khuyến mãi

    // Nội dung và hình ảnh
    private String description;     // Mô tả sách
    private String image;           // Hình sản phẩm

    // Trạng thái
    private int shop;           // Cho phép giao dịch
}