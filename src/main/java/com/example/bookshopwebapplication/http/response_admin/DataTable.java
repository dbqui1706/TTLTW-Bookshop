package com.example.bookshopwebapplication.http.response_admin;

import com.example.bookshopwebapplication.http.response_admin.invetory.InventorySummary;
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
    private InventorySummary summary;
}
