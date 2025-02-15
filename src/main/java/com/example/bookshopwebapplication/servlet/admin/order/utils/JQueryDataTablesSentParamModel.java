package com.example.bookshopwebapplication.servlet.admin.order.utils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JQueryDataTablesSentParamModel {

    /**
     * Request sequence number sent by DataTable, same value must be returned in
     * response
     */
    private int draw;

    /**
     * First record that should be shown(used for paging)
     */
    private int start;

    /**
     * Number of records that should be shown in table
     */
    private int length;

    /**
     * Global search criteria.
     */
    private JQueryDataTablesSearch search;

    /**
     * Column's ordering criteria.
     */
    private List<JQueryDataTablesOrder> order;

    /**
     * Table column's list.
     */
    private List<JQueryDataTablesColumn> columns;

}