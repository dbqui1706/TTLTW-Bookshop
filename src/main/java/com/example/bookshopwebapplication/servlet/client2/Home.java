package com.example.bookshopwebapplication.servlet.client2;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "")
public class Home extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                "<!DOCTYPE html><html><head><title>Bookstore HomePage</title></head><body>" +
                        "<script>" +
                        "  window.location.href = '/bookshop/index.html';" +
                        "</script>" +
                        "</body></html>"
        );
    }
}
