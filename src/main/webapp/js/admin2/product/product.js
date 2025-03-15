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

    // Button action
    const importExcelBtn = document.getElementById("importExcelBtn");
    const exportExcelBtn = document.getElementById("exportExcelBtn");
    const exportPdfBtn = document.getElementById("exportPdfBtn");
    const addProductBtn = document.getElementById("addProductBtn");

    // Biến đối tượng để đổ dữ liệu
    let productStatistic = {
        totalProducts: 0,
        available: 0,
        almostOutOfStock: 0,
        outOfStock: 0
    }
    let categories = {};
    let products = [];

    const stockFilterMap = {
        DEFAULT: "Tất cả",
        AVAILABLE: "Còn hàng",
        ALMOST_OUT_OF_STOCK: "Sắp hết",
        OUT_OF_STOCK: "Hết hàng"
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


    const getCategory = async () => {
        try {
            const response = await fetch(API_CATEGORIES, {
                method: "GET",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
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
                method: "GET",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                },
            });
            const status = response.status;
            if (status === 200) {
                const data = await response.json();
                productStatistic = data;
                console.log(productStatistic);

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
                    dropdownAutoWidth: true,
                    width: '100%'
                });
            });

            // Đặt chiều cao tối đa cho dropdown
            $('.select2-results__options').css('max-height', '300px');
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

    // Hàm lấy dữ liệu sản phẩm
    const getProducts = async (filter) => {
        try {
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
                method: "GET",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                },
            });

            const status = response.status;
            if (status === 200) {
                const data = await response.json();
                console.log(data);
            }
        } catch (error) {
            console.log(error);
        }
    }

    // ********* Tiến hành call và sử dụng API ********* //
    getCategory();
    getStatistic();
    setValueForFilter();
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

    FroalaEditor.DefineIcon('insertHTML', { NAME: 'plus', SVG_KEY: 'add' });
    FroalaEditor.RegisterCommand('insertHTML', {
        title: 'Insert HTML',
        focus: true,
        undo: true,
        refreshAfterCallback: true,
        callback: function () {
            this.html.insert('Some Custom HTML.');
            this.undo.saveStep();
        }
    });

    new FroalaEditor('div#froala-editor', {
        toolbarButtons: [
            ['bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'paragraphFormat', 'formatOL', 'formatUL'],
            ['fontFamily', 'fontSize', 'textColor', 'backgroundColor'],
            ['inlineClass', 'inlineStyle', 'clearFormatting'],
            ['insertLink', 'insertImage', 'insertVideo', 'insertTable', 'emoticons'],
            ['insertHTML', 'undo', 'redo', 'html'],
        ]
    })

    // Tải danh sách thể loại (trong trường hợp thực tế, bạn sẽ tải từ API)
    const categorySelect = document.getElementById('product-category');
    // Mảng thể loại sách mẫu
    const categories = [
        { id: 1, name: 'Văn học' },
        { id: 2, name: 'Kinh tế' },
        { id: 3, name: 'Tâm lý - Kỹ năng sống' },
        { id: 4, name: 'Thiếu nhi' },
        { id: 5, name: 'Tiểu sử - Hồi ký' },
        { id: 6, name: 'Giáo khoa - Tham khảo' },
        { id: 7, name: 'Ngoại ngữ' },
        { id: 8, name: 'Comics - Manga' }
    ];

    // Thêm các tùy chọn thể loại vào select
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.name;
        categorySelect.appendChild(option);
    });

});
