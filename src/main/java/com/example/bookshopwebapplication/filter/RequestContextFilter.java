package com.example.bookshopwebapplication.filter;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.utils.RequestContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class RequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        UserDto user = (UserDto) httpServletRequest.getSession().getAttribute("currentUser");
        if (user != null) {
            RequestContext.setUserId(user.getId());
            RequestContext.setUsername(user.getUsername());
        }
        RequestContext.setIpAddress(httpServletRequest.getRemoteAddr());
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
