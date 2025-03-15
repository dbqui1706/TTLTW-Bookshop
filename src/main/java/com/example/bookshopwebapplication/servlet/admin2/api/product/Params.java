package com.example.bookshopwebapplication.servlet.admin2.api.product;

public class Params {
    private int page;
    private int pageSize;
    private String search;
    private String sort;
    private String order;

    public Params() {
    }

    public Params(int page, int pageSize, String search, String sort, String order) {
        this.page = page;
        this.pageSize = pageSize;
        this.search = search;
        this.sort = sort;
        this.order = order;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getSearch() {
        return search;
    }

    public String getSort() {
        return sort;
    }

    public String getOrder() {
        return order;
    }
}
