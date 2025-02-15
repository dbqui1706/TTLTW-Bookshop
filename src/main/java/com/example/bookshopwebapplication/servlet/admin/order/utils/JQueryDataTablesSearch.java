package com.example.bookshopwebapplication.servlet.admin.order.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JQueryDataTablesSearch {
    /**
     * Search value. To be applied to all columns which have searchable as
     * true.
     */
    private String value;

    /**
     * true if the filter should be treated as a regular expression for
     * advanced searching, false otherwise.
     */
    private boolean regex;

}
