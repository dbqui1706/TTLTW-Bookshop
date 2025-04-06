package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.dto.CartDto;
import com.example.bookshopwebapplication.dto.CartItemDto;
import com.example.bookshopwebapplication.http.request.cart.AddCartRequest;
import com.example.bookshopwebapplication.http.request.cart.SaveCartRequest;
import com.example.bookshopwebapplication.http.request.cart.UpdateCart;
import com.example.bookshopwebapplication.http.response.product.CartProductResponse;
import com.example.bookshopwebapplication.service.CartItemService;
import com.example.bookshopwebapplication.service.CartService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.Protector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "CartController", urlPatterns = {
        "/api/cart",
        "/api/cart/save",
        "/api/cart/add",
        "/api/cart/update",
        "/api/cart/remove"
})
public class CartController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CartService cartService = new CartService();
    private CartItemService cartItemService = new CartItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        getCart(request, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    public void getCart(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy thông tin giỏ hàng từ request
            Long userId = Long.parseLong(request.getParameter("userId"));
            // Lấy thông tin giỏ hàng từ database
            Optional<CartDto> cart = cartService.getByUserId(userId);
            Long cartId = -1L;
            if (cart.isEmpty()) {
                // Tạo mới giỏ hàng
                cartId = cartService.newCart(userId);
            } else {
                cartId = cart.get().getId();
            }
            List<CartProductResponse> cartResponses = cartItemService.getByCartIdAndUserId(cartId, userId);

            JsonUtils.out(
                    response,
                    cartResponses,
                    HttpServletResponse.SC_OK
            );
            // Trả về thông tin giỏ hàng
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/cart/save":
                // Lưu giỏ hàng
                saveCart(req, resp);
                break;
            case "/api/cart/add":
                // Thêm sản phẩm vào giỏ hàng
                addToCart(req, resp);
                break;
            case "/api/cart/update":
                // Cập nhật giỏ hàng
                updateCart(req, resp);
                break;
            case "/api/cart/remove":
                // Xóa sản phẩm khỏi giỏ hàng
                removeCartItem(req, resp);
                break;
            default:
                JsonUtils.out(
                        resp,
                        "404 - Not found",
                        HttpServletResponse.SC_NOT_FOUND
                );
        }
    }

    private void removeCartItem(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Long cartItem = JsonUtils.get(req, Long.class);
            cartItemService.delete(new Long[]{cartItem});
            JsonUtils.out(
                    resp,
                    "Đã xóa sản phẩm khỏi giỏ hàng thành công!",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void updateCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UpdateCart updateCart = JsonUtils.get(req, UpdateCart.class);
            cartItemService.updateQuantity(updateCart.getCartItemId(), updateCart.getQuantity());
            JsonUtils.out(
                    resp,
                    "Đã cập nhật số lượng của sản phẩm thành công!",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void addToCart(HttpServletRequest req, HttpServletResponse resp) {
        try {
            AddCartRequest cartRequest = JsonUtils.get(req, AddCartRequest.class);
            Optional<CartDto> cart = cartService.getByUserId(cartRequest.getUserId());
            Long cartId = -1L;
            if (cart.isEmpty()) {
                // Tạo mới giỏ hàng
                cartId = cartService.newCart(cartRequest.getUserId());
            } else {
                cartId = cart.get().getId();
            }

            JsonUtils.out(
                    resp,
                    "Thêm sản phẩm vào giỏ hàng thành công",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void saveCart(HttpServletRequest req, HttpServletResponse resp) {
        try {
            SaveCartRequest cartRequest = JsonUtils.get(req, SaveCartRequest.class);
            // 1. Lấy giở hàng từ database với id người dùng
            // 1.1 Nếu không có giỏ hàng thì tạo mới
            // 1.2 Nếu có giỏ hàng thì lấy ra
            Optional<CartDto> cart = cartService.getByUserId(cartRequest.getUserId());
            Long cartId = -1L;
            if (cart.isEmpty()) {
                // Tạo mới giỏ hàng
                cartId = cartService.newCart(cartRequest.getUserId());
            } else {
                cartId = cart.get().getId();
            }
            // 2. Lưu các sản phẩm vào giỏ hàng theo bulk insert
            // 2.1 Lấy ra danh sách sản phẩm từ request
            // 2.2 Lưu vào database
            boolean result = cartItemService.bulkInsert(cartId, cartRequest.getCartItems());
            if (!result) {
                JsonUtils.out(
                        resp,
                        "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                return;
            }
            JsonUtils.out(
                    resp,
                    "Thêm sản phẩm vào giỏ hàng thành công",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
