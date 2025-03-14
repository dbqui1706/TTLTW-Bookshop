package com.example.bookshopwebapplication.servlet.admin2.api.product;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(name = "ControlPanel", urlPatterns = {
        "/admin2/api/product/add",
        "/admin2/api/product/edit",
        "/admin2/api/product/delete",
})
public class ControlPanel extends HttpServlet {
}
