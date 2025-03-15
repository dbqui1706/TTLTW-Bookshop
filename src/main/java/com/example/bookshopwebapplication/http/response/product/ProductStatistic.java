package com.example.bookshopwebapplication.http.response.product;

import lombok.Data;

@Data
public class ProductStatistic {
    private int total;
    private int available;
    private int almostOutOfStock;
    private int outOfStock;
}
