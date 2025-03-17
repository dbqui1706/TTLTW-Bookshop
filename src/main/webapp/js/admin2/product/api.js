// ==========================================================
// api.js - Chứa các hằng số API và hàm gọi API
// ==========================================================

/**
 * Định nghĩa các URL API
 * @type {{
 *  PRODUCTS: string,
 *  STATISTIC: string,
 *  CATEGORIES: string
 * }}
 */
export const API_URLS = {
    STATISTIC: "/admin2/api/product/statistic",
    PRODUCTS: "/admin2/api/product/table",
    CATEGORIES: "/admin2/api/product/category"
};

const headers = {
    "Accept": "application/json",
    "Content-Type": "application/json;charset=UTF-8",
}

/**
 * Lấy thông tin danh mục sản phẩm
 * @returns {Promise<{}|any>} : Danh sách {id: name} của các danh mục
 */
export const getCategory = async () => {
    try {
        const response = await fetch(API_URLS.CATEGORIES, {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json;charset=UTF-8",
            },
        });
        if (response.status === 200) {
            return await response.json();
        }
        return {};
    } catch (error) {
        console.log(error);
        return {};
    }
};


/**
 * Lấy thông tin thống kê sản phẩm cho trang quản lý sản phẩm
 * phần thống kê bao gồm: tổng số sản phẩm, số sản phẩm còn hàng,
 * số sản phẩm sắp hết hàng, số sản phẩm hết hàng
 *
 * @returns {Promise<Object>} Thông tin thống kê
 */
export const getStatistic = async () => {
    try {
        const response = await fetch(API_URLS.STATISTIC, {
            method: "GET",
            headers: headers,
        });
        if (response.status === 200) {
            return await response.json();
        }
        return {
            total: 0,
            available: 0,
            almostOutOfStock: 0,
            outOfStock: 0
        };
    } catch (error) {
        console.log(error);
        return {
            total: 0,
            available: 0,
            almostOutOfStock: 0,
            outOfStock: 0
        };
    }
};

/**
 * Lấy danh sách sản phẩm theo bộ lọc
 * @param filter
 * @returns {Promise<any|{totalProducts: number, totalPages: number, currentPage: number, products: *[]}>}
 */
export const getProducts = async (filter) => {
    try {
        const queryParams = new URLSearchParams();
        if (filter.category) queryParams.append("category", filter.category);
        if (filter.stock) queryParams.append("stock", filter.stock);
        if (filter.sortOption) queryParams.append("sortOption", filter.sortOption);
        if (filter.search) queryParams.append("search", filter.search);
        if (filter.page) queryParams.append("page", filter.page);
        if (filter.limit) queryParams.append("limit", filter.limit);

        const url = `${API_URLS.PRODUCTS}?${queryParams.toString()}`;
        const response = await fetch(url, {
            method: "GET",
            headers: headers,
        });

        if (response.status === 200) {
            return await response.json();
        }
        return { products: [], currentPage: 1, totalPages: 1, totalProducts: 0 };
    } catch (error) {
        console.log(error);
        return { products: [], currentPage: 1, totalPages: 1, totalProducts: 0 };
    }
};