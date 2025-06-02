// =======================================================================
// main.js - File chính kết nối tất cả module cho trang quản lý sản phẩm
// =======================================================================
import { getCategory } from '../services/ProductService.js'
import {  initializeSelect2 } from "../core/utils.js";
import {
    filterInitialize,
    loadProducts,
    setupFilterEvents,
    setValueForFilter,
    setDefaultItemsPerPage,
    loadSatistic
} from '../containers/products/ProductContainer.js';


export const stockFilterMap = {
    DEFAULT: "Tất cả",
    AVAILABLE: "Còn hàng",
    ALMOST_OUT_OF_STOCK: "Sắp hết",
    OUT_OF_STOCK: "Hết hàng"
};

export const badgeStockMap = {
    'AVAILABLE': `<span class="badge bg-success">Còn hàng</span>`,
    'ALMOST_OUT_OF_STOCK': `<span class="badge bg-warning text-dark">Sắp hết</span>`,
    'OUT_OF_STOCK': `<span class="badge bg-danger">Hết hàng</span>`
};

export const sortOptionMap = {
    DEFAULT: "Mặc định",
    PRICE_ASC: "Giá tăng dần",
    PRICE_DESC: "Giá giảm dần",
    NAME_ASC: "Tên A-Z",
    NAME_DESC: "Tên Z-A",
    POPULARITY_ASC: "Phổ biến nhất",
    CREATED_AT_ASC: "Cũ nhất",
    CREATED_AT_DESC: "Mới nhất"
};

document.addEventListener("DOMContentLoaded", async function () {

    // Lấy và hiển thị danh mục
    const categories = await getCategory();
    if (categories) {
        const categoryFilter = document.getElementById("categoryFilter");
        if (categoryFilter) {
            Object.entries(categories).forEach(([id, name]) => {
                const option = document.createElement("option");
                option.value = id;
                option.textContent = name;
                categoryFilter.appendChild(option);
            });
        }
    }

    loadSatistic();

    // Thiết lập các giá trị và sự kiện cho filter
    setValueForFilter(stockFilterMap, sortOptionMap);

    // Khởi tạo Select2 cho các dropdown
    initializeSelect2();

    // Thiết lập sự kiện cho các filter
    setupFilterEvents();

    // Thiết lập giá trị mặc định cho items per page
    setDefaultItemsPerPage();

    // Tải sản phẩm
    loadProducts();
});