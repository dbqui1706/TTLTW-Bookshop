// View toggle functionality
document.addEventListener("DOMContentLoaded", function () {
    // Urls API
    const API_STATISTIC = "/admin2/api/product/statistic";
    const API_PRODUCTS = "/admin2/api/product/table";
    const API_CATEGORIES = "/admin2/api/product/category";

    // ID của các thẻ hiển thị thông tin thống kê
    const totalProducts = document.getElementById("totalProducts");
    const available = document.getElementById("available");
    const almostOutOfStock = document.getElementById("almostOutOfStock");
    const outOfStock = document.getElementById("outOfStock");
    const categoryFilter = document.getElementById("categoryFilter");
    const stockFilter = document.getElementById("stockFilter");
    const searchInput = document.getElementById("searchProduct");
    const itemsPerPageSelect = document.getElementById("itemsPerPage");

    // Button action
    const importExcelBtn = document.getElementById("importExcelBtn");
    const exportExcelBtn = document.getElementById("exportExcelBtn");
    const exportPdfBtn = document.getElementById("exportPdfBtn");
    const addProductBtn = document.getElementById("addProductBtn");

    // Biến đối tượng để đổ dữ liệu
    let productStatistic = {
        totalProducts: 0, available: 0, almostOutOfStock: 0, outOfStock: 0
    }
    let categories = {};
    let products = [];

    const stockFilterMap = {
        DEFAULT: "Tất cả", AVAILABLE: "Còn hàng", ALMOST_OUT_OF_STOCK: "Sắp hết", OUT_OF_STOCK: "Hết hàng"
    };

    const badgeStockMap = {
        'AVAILABLE': `<span class="badge bg-success">Còn hàng</span>`,
        'ALMOST_OUT_OF_STOCK': `<span class="badge bg-warning text-dark">Sắp hết</span>`,
        'OUT_OF_STOCK': `<span class="badge bg-danger">Hết hàng</span>`
    };

    const sortOptionMap = {
        DEFAULT: "Mặc định",
        PRICE_ASC: "Giá tăng dần",
        PRICE_DESC: "Giá giảm dần",
        NAME_ASC: "Tên A-Z",
        NAME_DESC: "Tên Z-A",
        POPULARITY_ASC: "Phổ biến nhất",
        CREATED_AT_ASC: "Cũ nhất",
        CREATED_AT_DESC: "Mới nhất"
    };

    let filterInitialize = {
        category: "",
        stock: "DEFAULT",
        sortOption: "DEFAULT",
        search: "",
        page: 1,
        totalPages: 1,
        limit: 10,
        data: []
    };


    // Hàm lấy danh sách các thể loại
    const getCategory = async () => {
        try {
            const response = await fetch(API_CATEGORIES, {
                method: "GET", headers: {
                    "Accept": "application/json", "Content-Type": "application/json",
                },
            });
            const status = response.status;
            if (status === 200) {
                const data = await response.json();
                categories = data;
                console.log(categories);
                Object.entries(categories).forEach(([id, name]) => {
                    const option = document.createElement("option");
                    option.value = id;
                    option.textContent = name;
                    categoryFilter.appendChild(option);
                });

                // Khởi tạo Select2 sau khi đã thêm tất cả các option
                initializeSelect2();
            }
        } catch (error) {
            console.log(error);
        }
    }

    // Lấy dữ liệu thống kê và hiển thị
    const getStatistic = async () => {
        try {
            const response = await fetch(API_STATISTIC, {
                method: "GET", headers: {
                    "Accept": "application/json", "Content-Type": "application/json",
                },
            });
            const status = response.status;
            if (status === 200) {
                const data = await response.json();
                productStatistic = data;

                totalProducts.textContent = productStatistic.total;
                available.textContent = productStatistic.available;
                almostOutOfStock.textContent = productStatistic.almostOutOfStock;
                outOfStock.textContent = productStatistic.outOfStock;
            }
        } catch (error) {
            console.log(error);
        }
    }

    // Hàm khởi tạo Select2
    const initializeSelect2 = () => {
        // Kiểm tra xem jQuery và Select2 đã được tải chưa
        if (typeof jQuery !== 'undefined' && typeof jQuery.fn.select2 !== 'undefined') {
            $('.form-select').each(function () {
                $(this).select2({
                    minimumResultsForSearch: Infinity, // Ẩn ô tìm kiếm
                    dropdownAutoWidth: true, width: '100%'
                });
            });

            // Đặt chiều cao tối đa cho dropdown
            $('.select2-results__options').css('max-height', '400px');
        } else {
            console.error("jQuery hoặc Select2 chưa được tải!");
        }
    };

    // Hàm thiết lập giá trị cho filter
    const setValueForFilter = () => {
        Object.entries(stockFilterMap).forEach(([key, value]) => {
            const option = document.createElement("option");
            option.value = key;
            option.textContent = value;
            stockFilter.appendChild(option);
        });

        Object.entries(sortOptionMap).forEach(([key, value]) => {
            const option = document.createElement("option");
            option.value = key;
            option.textContent = value;
            document.getElementById("sortOption").appendChild(option);
        });
    }

    // Cập nhật hàm getProducts để hiển thị dữ liệu trả về
    const getProducts = async (filter) => {
        try {

            showLoading();
            // Tạo query string từ filter
            const queryParams = new URLSearchParams();

            // Thêm các tham số vào URL nếu có giá trị
            if (filter.category) queryParams.append("category", filter.category);
            if (filter.stock) queryParams.append("stock", filter.stock);
            if (filter.sortOption) queryParams.append("sortOption", filter.sortOption);
            if (filter.search) queryParams.append("search", filter.search);
            if (filter.page) queryParams.append("page", filter.page);
            if (filter.limit) queryParams.append("limit", filter.limit);

            const url = `${API_PRODUCTS}?${queryParams.toString()}`;
            const response = await fetch(url, {
                method: "GET", headers: {
                    "Accept": "application/json", "Content-Type": "application/json",
                },
            });

            const status = response.status;
            if (status === 200) {
                const data = await response.json();
                console.log(data);
                filterInitialize.data = data.products;
                // Render sản phẩm vào bảng
                renderProductTable(data.products);

                // Cập nhật phân trang
                updatePagination(data.currentPage, data.totalPages);

                // Cập nhật tổng số sản phẩm hiển thị
                document.querySelector('.col-md-6.mb-3 span.ms-2').textContent = `sản phẩm (Tổng số: ${data.totalProducts})`;
            }
        } catch (error) {
            console.log(error);
        } finally {
            hideLoading();
        }
    };

    // Hàm render sản phẩm vào bảng
    const renderProductTable = (products) => {
        const tableBody = document.getElementById('productTable').querySelector('tbody');
        tableBody.innerHTML = '';

        products.forEach(product => {
            // Tạo trạng thái tồn kho và màu sắc
            let stockStatus, stockBadgeClass, stockIndicatorClass;
            if (product.stockStatus === 'OUT_OF_STOCK') {
                stockStatus = 'Hết hàng';
                stockBadgeClass = 'bg-danger';
                stockIndicatorClass = 'stock-low';
            } else if (product.stockStatus === 'ALMOST_OUT_OF_STOCK') {
                stockStatus = 'Sắp hết';
                stockBadgeClass = 'bg-warning text-dark';
                stockIndicatorClass = 'stock-medium';
            } else {
                stockStatus = 'Còn hàng';
                stockBadgeClass = 'bg-success';
                stockIndicatorClass = 'stock-high';
            }

            // Format giá tiền
            const formatPrice = (price) => {
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                    maximumFractionDigits: 0
                }).format(price);
            };

            // Tạo hàng mới trong bảng
            const row = document.createElement('tr');
            row.innerHTML = `
            <td>#${product.id}</td>
            <td>
                <img src="${product.imageName ? product.imageName : 'https://via.placeholder.com/300x300'}" 
                     alt="${product.name}" width="50" height="50" class="rounded"/>
            </td>
            <td>${product.name}</td>
            <td>
                ${product.categoryName}
            </td>
            <td class="fw-bold">${formatPrice(product.discountedPrice)}</td>
            <td class="${product.discountedPrice === product.price ? '' : 'text-decoration-line-through'}">
                ${formatPrice(product.price)}
            </td>
            <td>
                <div class="d-flex align-items-center">
                    <div class="stock-indicator ${stockIndicatorClass} me-2"></div>
                    <span>${product.quantity}</span>
                </div>
            </td>
            <td><span class="badge ${stockBadgeClass}">${stockStatus}</span></td>
            <td>${product.totalBuy}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-secondary" data-product-id="${product.id}" data-action="view">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-outline-primary" data-product-id="${product.id}" data-action="edit">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-outline-danger" data-product-id="${product.id}" data-action="delete">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
            tableBody.appendChild(row);
        });

        // Thêm sự kiện cho các nút
        addButtonEventListeners();
    };

    // Hàm thêm sự kiện cho các nút trong bảng
    const addButtonEventListeners = () => {
        // Nút xem chi tiết
        document.querySelectorAll('[data-action="view"]').forEach(button => {
            button.addEventListener('click', function () {
                const productId = this.getAttribute('data-product-id');
                viewProduct(productId);
            });
        });

        // Nút sửa sản phẩm

        // Nút xóa sản phẩm
    };

    // Hàm cập nhật phân trang
    const updatePagination = (currentPage, totalPages) => {
        const pagination = document.querySelector('.pagination');
        pagination.innerHTML = '';

        // Nút Previous
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
        prevLi.innerHTML = `
        <a class="page-link" href="#" aria-label="Previous" data-page="${currentPage - 1}">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
        pagination.appendChild(prevLi);

        // Các nút trang
        const maxPagesToShow = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if (endPage - startPage + 1 < maxPagesToShow) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
            pagination.appendChild(pageLi);
        }

        // Nút Next
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = `
        <a class="page-link" href="#" aria-label="Next" data-page="${currentPage + 1}">
            <span aria-hidden="true">&raquo;</span>
        </a>
    `;
        pagination.appendChild(nextLi);

        // Thêm sự kiện cho các nút phân trang
        document.querySelectorAll('.pagination .page-link').forEach(link => {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                if (page > 0 && page <= totalPages) {
                    filterInitialize.page = page;
                    getProducts(filterInitialize);
                }
            });
        });
    };

    // Hàm xem chi tiết sản phẩm
    const viewProduct = (productId) => {
        // Đây là nơi bạn sẽ thêm code để hiển thị modal chi tiết sản phẩm
        // Bạn cần tạo một API endpoint riêng để lấy chi tiết sản phẩm theo ID
        console.log('View product:', productId);
        // Điền thông tin vào modal xem chi tiết
        const productDetails = filterInitialize.data.find(product => product.id == productId);
        console.log("productDetails: ", productDetails);

        document.getElementById('viewProductId').textContent = productDetails.id;
        document.getElementById('viewProductName').textContent = productDetails.name;
        document.getElementById('viewCategory').textContent = productDetails.categoryName;
        document.getElementById('viewProductPrice').textContent = `Giá: ${formatCurrency(productDetails.discountedPrice)}`;
        if (productDetails.discountedPrice !== productDetails.price) {
            document.getElementById('viewProductOriginalPrice').textContent = formatCurrency(productDetails.price);
        }
        document.getElementById('viewProductQuantity').textContent = productDetails.quantity;
        document.getElementById('viewProductStatus').innerHTML = badgeStockMap[productDetails.stockStatus];
        document.getElementById('viewProductImage').src = productDetails.imageName ? productDetails.imageName : 'https://via.placeholder.com/300x300';
        document.getElementById('viewProductAuthor').textContent = productDetails.author;
        document.getElementById('viewProductPublisher').textContent = productDetails.publisher;
        document.getElementById('viewProductYear').textContent = productDetails.yearPublishing;
        document.getElementById('viewProductPages').textContent = productDetails.pages;
        document.getElementById('viewProductTotalBuy').textContent = productDetails.totalBuy;
        document.getElementById('viewProductDescription').innerHTML = productDetails.description;
        document.getElementById('viewProductDiscount').textContent = `${productDetails.discount}%`;
        ;document.getElementById('viewProductShop').innerHTML = productDetails.shop === 0 ? `<span class="badge bg-success">Có</span>` : `<span class="badge bg-danger">Không</span>`;
        document.getElementById('viewProductStartsAt').textContent = productDetails.startsAt ? formatDateTime(productDetails.startsAt) : "Không có";
        document.getElementById('viewProductEndsAt').textContent = productDetails.endsAt ? formatDateTime(productDetails.endsAt) : "Không có";

        // Mở modal xem chi tiết
        const viewModal = new bootstrap.Modal(document.getElementById('viewProductModal'));
        viewModal.show();
    };

    // Hàm format tiền tệ
    const formatCurrency = (price) => {
        cnsole.log('formatCurrency received:', price, typeof price);
        // Kiểm tra nếu price không phải là số hoặc bị NaN
        if (typeof price !== 'number' || isNaN(price)) {
            return 'N/A'; // Hoặc trả về một giá trị mặc định
        }

        return new Intl.NumberFormat('vi-VN', {
            style: 'currency', currency: 'VND', maximumFractionDigits: 0
        }).format(price);
    };

    // Hàm format ngày tháng
    const formatDateTime = (date) => {
        return new Intl.DateTimeFormat('vi-VN', {
            dateStyle: 'full', timeStyle: 'long'
        }).format(new Date(date));
    }

    // Thêm hàm utility để hiển thị và ẩn loading
    const showLoading = () => {
        const loadingOverlay = document.getElementById('loadingOverlay');
        if (loadingOverlay) {
            loadingOverlay.style.display = 'flex';
            // Thêm class show sau một tick để trigger animation nếu có
            setTimeout(() => loadingOverlay.classList.add('show'), 10);
        }
    };

    const hideLoading = () => {
        const loadingOverlay = document.getElementById('loadingOverlay');
        if (loadingOverlay) {
            loadingOverlay.classList.remove('show');
            // Chờ animation hoàn thành rồi mới ẩn hoàn toàn
            setTimeout(() => {
                loadingOverlay.style.display = 'none';
            }, 300);
        }
    };

    // Thiết lập giá trị mặc định cho select box
    const setDefaultItemsPerPage = () => {
        // Đặt giá trị mặc định cho select box là 10
        itemsPerPageSelect.value = filterInitialize.limit.toString();
    }

    // Hàm setup các sự kiện cho filter
    const setupFilterEvents = () => {
        // Thêm sự kiện cho select box items per page
        itemsPerPageSelect.addEventListener('change', function () {
            filterInitialize.limit = parseInt(this.value);
            filterInitialize.page = 1; // Reset về trang 1 khi thay đổi số lượng hiển thị
            getProducts(filterInitialize);
        });

    }

    // ******************* Call API ******************** //
    getCategory();
    getStatistic();
    setValueForFilter();
    setupFilterEvents();
    setDefaultItemsPerPage(); // Thiết lập giá trị mặc định cho select box
    getProducts(filterInitialize);
    // ************************************************* //
});

document.addEventListener('DOMContentLoaded', function () {
    // Lấy tham chiếu đến nút "Thêm sản phẩm" hiện có
    const addProductBtn = document.getElementById('addProductBtn');

    // Sửa hành vi của nút để mở modal thay vì chuyển trang
    if (addProductBtn) {
        // addProductBtn.setAttribute('href', '#');
        addProductBtn.setAttribute('data-bs-toggle', 'modal');
        addProductBtn.setAttribute('data-bs-target', '#addProductModal');
    }

    // Xử lý tải lên và xem trước hình ảnh
    const imageInput = document.getElementById('product-imageName');
    const imagePreview = document.getElementById('imagePreview');
    const previewImage = imagePreview.querySelector('img');

    imageInput.addEventListener('change', function () {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImage.src = e.target.result;
                imagePreview.style.display = 'block';
            };
            reader.readAsDataURL(this.files[0]);
        } else {
            imagePreview.style.display = 'none';
        }
    });

    // Xử lý nút reset form
    const resetFormBtn = document.getElementById('resetFormBtn');
    const addProductForm = document.getElementById('addProductForm');

    resetFormBtn.addEventListener('click', function () {
        if (confirm('Bạn có muốn đặt lại form về giá trị mặc định?')) {
            addProductForm.reset();
            imagePreview.style.display = 'none';

            // Xóa các thông báo lỗi
            document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
            document.querySelectorAll('.is-valid').forEach(el => el.classList.remove('is-valid'));
            document.getElementById('successMessage').style.display = 'none';
            document.getElementById('errorMessage').style.display = 'none';
        }
    });

    // Xử lý nút lưu sản phẩm (thêm sản phẩm)
    const saveProductBtn = document.getElementById('saveProductBtn');

    saveProductBtn.addEventListener('click', function () {
        if (addProductForm.checkValidity()) {
            // Trong trường hợp thực tế, ở đây bạn sẽ gửi form bằng AJAX
            // Tuy nhiên, vì đây là demo, chúng ta sẽ giả lập việc gửi form thành công

            // Hiển thị thông báo thành công
            const successMessage = document.getElementById('successMessage');
            successMessage.textContent = 'Sản phẩm đã được thêm thành công!';
            successMessage.style.display = 'block';

            // Ẩn thông báo sau 3 giây và đóng modal
            setTimeout(function () {
                const modal = bootstrap.Modal.getInstance(document.getElementById('addProductModal'));
                modal.hide();
                successMessage.style.display = 'none';
                addProductForm.reset();
                imagePreview.style.display = 'none';
            }, 3000);

            // Trong trường hợp thực tế, bạn có thể muốn gửi form như sau:
            // addProductForm.submit();
        } else {
            // Hiển thị thông báo lỗi
            const errorMessage = document.getElementById('errorMessage');
            errorMessage.textContent = 'Vui lòng điền đầy đủ thông tin bắt buộc!';
            errorMessage.style.display = 'block';

            // Highlight các trường bắt buộc chưa điền
            addProductForm.reportValidity();
        }
    });

    FroalaEditor.DefineIcon('insertHTML', {NAME: 'plus', SVG_KEY: 'add'});
    FroalaEditor.RegisterCommand('insertHTML', {
        title: 'Insert HTML', focus: true, undo: true, refreshAfterCallback: true, callback: function () {
            this.html.insert('Some Custom HTML.');
            this.undo.saveStep();
        }
    });

    new FroalaEditor('div#froala-editor', {
        toolbarButtons: [['bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'paragraphFormat', 'formatOL', 'formatUL'], ['fontFamily', 'fontSize', 'textColor', 'backgroundColor'], ['inlineClass', 'inlineStyle', 'clearFormatting'], ['insertLink', 'insertImage', 'insertVideo', 'insertTable', 'emoticons'], ['insertHTML', 'undo', 'redo', 'html'],]
    })

    // Tải danh sách thể loại (trong trường hợp thực tế, bạn sẽ tải từ API)
    const categorySelect = document.getElementById('product-category');
    // Mảng thể loại sách mẫu
    const categories = [{id: 1, name: 'Văn học'}, {id: 2, name: 'Kinh tế'}, {
        id: 3,
        name: 'Tâm lý - Kỹ năng sống'
    }, {id: 4, name: 'Thiếu nhi'}, {id: 5, name: 'Tiểu sử - Hồi ký'}, {id: 6, name: 'Giáo khoa - Tham khảo'}, {
        id: 7,
        name: 'Ngoại ngữ'
    }, {id: 8, name: 'Comics - Manga'}];

    // Thêm các tùy chọn thể loại vào select
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.name;
        categorySelect.appendChild(option);
    });

});
