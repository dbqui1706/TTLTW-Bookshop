package com.example.bookshopwebapplication.servlet.admin.order.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.*;

public class Utils {
    public static JQueryDataTablesSentParamModel getParam(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();

        if(parameterNames.hasMoreElements()) {
            JQueryDataTablesSentParamModel param = new JQueryDataTablesSentParamModel();

            param.setDraw(Integer.parseInt(request.getParameter("draw")));
            param.setStart(Integer.parseInt(request.getParameter("start")));
            param.setLength(Integer.parseInt(request.getParameter("length")));

            JQueryDataTablesSearch globalSearch = new JQueryDataTablesSearch();
            globalSearch.setValue(request.getParameter("search[value]"));
            globalSearch.setRegex(Boolean.parseBoolean(request.getParameter("search[regex]")));
            param.setSearch(globalSearch);

            List<JQueryDataTablesOrder> orders = new ArrayList<>();
            int orderCnt = Utils.getNumberOfArray(request, "order");

            for (int i = 0; i < orderCnt; i++) {
                JQueryDataTablesOrder order = new JQueryDataTablesOrder();

                order.setColumn(Integer.parseInt(request.getParameter("order[" + i + "][column]")));
                order.setDir(request.getParameter("order[" + i + "][dir]"));

                orders.add(order);
            }

            param.setOrder(orders);

            List<JQueryDataTablesColumn> columns = new ArrayList<>();
            int columnCnt = Utils.getNumberOfArray(request, "columns");

            for (int i = 0; i < columnCnt; i++) {
                JQueryDataTablesColumn column = new JQueryDataTablesColumn();
                column.setData(request.getParameter("columns[" + i + "][data]"));
                column.setName(request.getParameter("columns[" + i + "][name]"));
                column.setSearchable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][searchable]")));
                column.setOrderable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][orderable]")));

                JQueryDataTablesSearch search = new JQueryDataTablesSearch();
                search.setValue(request.getParameter("columns[" + i + "][search][value]"));
                search.setRegex(Boolean.parseBoolean(request.getParameter("columns[" + i + "][search][regex]")));

                column.setSearch(search);

                columns.add(column);
            }

            param.setColumns(columns);

            return param;
        } else {
            return null;
        }
    }

    private static int getNumberOfArray(HttpServletRequest request, String arrayName) {
        Pattern pattern = null;

        switch (arrayName) {
            case "columns":
                pattern = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
                break;
            case "order":
                pattern = Pattern.compile("order\\[[0-9]+\\]\\[column\\]");
                break;
            default:
                break;
        }

        @SuppressWarnings("rawtypes")
        Enumeration params = request.getParameterNames();
        List<String> array = new ArrayList<String>();

        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            Matcher matcher = pattern.matcher(paramName);
            if (matcher.matches()) {
                array.add(paramName);
            }
        }

        return array.size();
    }
}
