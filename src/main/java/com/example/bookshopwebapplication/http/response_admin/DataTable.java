package com.example.bookshopwebapplication.http.response_admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataTable<T> {
    private List<T> data;
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
}
