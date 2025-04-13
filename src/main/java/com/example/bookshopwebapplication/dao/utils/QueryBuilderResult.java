package com.example.bookshopwebapplication.dao.utils;

import lombok.Builder;

import java.util.List;

/**
 * Lớp kết quả chứa SQL và các tham số được tạo từ QueryBuilder
 */
@Builder
public record QueryBuilderResult(String sql, List<Object> params) {
}

