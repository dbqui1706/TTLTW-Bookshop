package com.example.bookshopwebapplication.http.request.product;

import lombok.Data;

@Data
public class ProductStatistic {
    private int total;
    private int available;
    private int almostOutOfStock;
    private int outOfStock;
}
