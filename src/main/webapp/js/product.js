import createToast, {toastComponent} from "./toast.js";
import {setTotalCartItemsQuantity} from "./header.js";

// STATIC DATA
const contextPathMetaTag = document.querySelector("meta[name='contextPath']");
const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");
const productIdMetaTag = document.querySelector("meta[name='productId']");

const quantityInput = document.querySelector("#cart-item-quantity");
const productTitleElement = document.querySelector(".title");

const currentProduct = document.querySelector("meta[name='currentProduct']");

// Xóa tên đối tượng bên ngoài
const rawObject = currentProduct.content.replace(/^ProductDto\(|\)$/g, "");

// Chuyển đổi chuỗi thành một đối tượng
const parsedObject = rawObject.split(", ").reduce((acc, item) => {
    const [key, ...value] = item.split("=");
    acc[key.trim()] = value.join("=").trim(); // Ghép lại phần giá trị nếu có dấu "="
    return acc;
}, {});

// Hiển thị kết quả
console.log(parsedObject.id, parsedObject.price, parsedObject.discount, parsedObject.quantity);


// MESSAGES
const REQUIRED_SIGNIN_MESSAGE = "Vui lòng đăng nhập để thực hiện thao tác!";
const SUCCESS_ADD_CART_ITEM_MESSAGE = (quantity, productTitle) =>
    `Đã thêm thành công ${quantity} sản phẩm ${productTitle} vào giỏ hàng!`;
const FAILED_ADD_CART_ITEM_MESSAGE = "Đã có lỗi truy vấn!";
const SUCCESS_ADD_WISHLIST_ITEM_MESSAGE = (productTitle) =>
    `Đã thêm thành công sản phẩm ${productTitle} vào danh sách yêu thích!`;
const FAILED_ADD_WISHLIST_ITEM_MESSAGE = "Đã có lỗi truy vấn!";

// UTILS
async function _fetchPostAddCartItem() {
    const cartItemRequest = {
        userId: currentUserIdMetaTag.content,
        productId: productIdMetaTag.content,
        quantity: quantityInput.value,
    };

    const response = await fetch(contextPathMetaTag.content + "/cartItem", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(cartItemRequest),
    });

    return [response.status, await response.json()];
}

async function _fetchPutAddWishlistItem() {
    const wishlistItemRequest = {
        userId: currentUserIdMetaTag.content,
        productId: productIdMetaTag.content,
    };

    const response = await fetch(contextPathMetaTag.content + "/wishlist", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(wishlistItemRequest),
    });

    return [response.status, await response.json()];
}

// EVENT HANDLERS
function noneSigninEvent() {
    createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE));
}

async function addWishlistItemBtnEvent() {
    const [status] = await _fetchPutAddWishlistItem();
    if (status === 200) {
        createToast(toastComponent(
            SUCCESS_ADD_WISHLIST_ITEM_MESSAGE(productTitleElement.innerText), "success"));
        addWishlistItemBtn.disabled = true;
    } else if (status === 404) {
        createToast(toastComponent(FAILED_ADD_WISHLIST_ITEM_MESSAGE, "danger"));
    }
}

async function addCartItemBtnEvent() {
    const [status] = await _fetchPostAddCartItem();
    if (status === 200) {
        createToast(toastComponent(
            SUCCESS_ADD_CART_ITEM_MESSAGE(quantityInput.value, productTitleElement.innerText), "success"));
        setTotalCartItemsQuantity(quantityInput.value);
    } else if (status === 404) {
        createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
    }
}

// MAIN
const addWishlistItemBtn = document.querySelector("#add-wishlist-item");
const buyNowBtn = document.querySelector("#buy-now");
const addCartItemBtn = document.querySelector("#add-cart-item");

if (currentUserIdMetaTag) {
    addWishlistItemBtn.addEventListener("click", addWishlistItemBtnEvent)
    buyNowBtn.addEventListener("click", async () => {
        const orderRequest = {
            cartId: -1,
            userId: currentUserIdMetaTag.content,
            orderItems: [
                {
                    productId: parsedObject.id,
                    price: parsedObject.price  * (1 - parsedObject.discount / 100),
                    discount: parsedObject.discount,
                    quantity: quantityInput.value,
                }
            ],
        };

        // Gửi dữ liệu tới backend
        const response = await fetch(contextPathMetaTag.content + "/order-info", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(orderRequest),
        });

        if (response.ok) {
            // Điều hướng tới trang order-info.jsp thông qua RequestDispatcher trong Servlet
            window.location.href = contextPathMetaTag.content + "/order-info";
        } else {
            console.error("Failed to place the order.");
        }
    });
    addCartItemBtn.addEventListener("click", addCartItemBtnEvent);
} else {
    addWishlistItemBtn.addEventListener("click", noneSigninEvent);
    buyNowBtn.addEventListener("click", noneSigninEvent);
    addCartItemBtn.addEventListener("click", noneSigninEvent);
}
