// View toggle functionality
document.addEventListener("DOMContentLoaded", function () {
    // ID của các thẻ hiển thị thông tin thống kê
    const totalProducts = document.getElementById("totalProducts");
    const available = document.getElementById("available");
    const almostOutOfStock = document.getElementById("almostOutOfStock");
    const outOfStock = document.getElementById("outOfStock");

    // Biến đối tượng để đổ dữ liệu
    let productStatistic = {
        totalProducts: 0,
        available: 0,
        almostOutOfStock: 0,
        outOfStock: 0
    }

    // Urls API
    const API_STATISTIC = "/admin2/api/product/statistic";
    const API_PRODUCTS = "/admin2/api/product";

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

    // ********* Tiến hành call và sử dụng API ********* //
    getStatistic();

    // ************************************************* //
    const viewOptions = document.querySelectorAll(".view-option");
    const productContainers =
        document.querySelectorAll(".product-container");

    viewOptions.forEach((option) => {
        option.addEventListener("click", function () {
            // Remove active class from all options
            viewOptions.forEach((opt) => opt.classList.remove("active"));

            // Add active class to clicked option
            this.classList.add("active");

            // Get the view type
            const viewType = this.getAttribute("data-view");

            // Hide all product containers
            productContainers.forEach((container) => {
                container.classList.add("d-none");
            });

            // Show the selected view
            document
                .getElementById(viewType + "-view")
                .classList.remove("d-none");
        });
    });

    // Search functionality
    const searchInput = document.getElementById("searchProduct");
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const searchValue = this.value.toLowerCase();
            const productItems = document.querySelectorAll(".product-item");

            productItems.forEach((item) => {
                const productName = item
                    .querySelector(".card-title")
                    .textContent.toLowerCase();
                const productId = item
                    .querySelector(".text-muted")
                    .textContent.toLowerCase();

                if (
                    productName.includes(searchValue) ||
                    productId.includes(searchValue)
                ) {
                    item.style.display = "";
                } else {
                    item.style.display = "none";
                }
            });
        });
    }

    // Category filter
    const categoryFilter = document.getElementById("categoryFilter");
    if (categoryFilter) {
        categoryFilter.addEventListener("change", function () {
            const selectedCategory = this.value;
            const productItems = document.querySelectorAll(".product-item");

            if (selectedCategory === "") {
                productItems.forEach((item) => {
                    item.style.display = "";
                });
                return;
            }

            productItems.forEach((item) => {
                const categoryBadge = item
                    .querySelector(".badge")
                    .textContent.toLowerCase();
                const categoryMap = {
                    1: "điện thoại",
                    2: "laptop",
                    3: "máy tính bảng",
                    4: "phụ kiện",
                };

                if (
                    categoryMap[selectedCategory] === categoryBadge.toLowerCase()
                ) {
                    item.style.display = "";
                } else {
                    item.style.display = "none";
                }
            });
        });
    }

    // Stock filter
    const stockFilter = document.getElementById("stockFilter");
    if (stockFilter) {
        stockFilter.addEventListener("change", function () {
            const selectedStock = this.value;
            const productItems = document.querySelectorAll(".product-item");

            if (selectedStock === "") {
                productItems.forEach((item) => {
                    item.style.display = "";
                });
                return;
            }

            productItems.forEach((item) => {
                const badge = item
                    .querySelector(".badge.position-absolute")
                    .textContent.toLowerCase();

                if (
                    (selectedStock === "in-stock" && badge === "còn hàng") ||
                    (selectedStock === "low-stock" && badge === "sắp hết") ||
                    (selectedStock === "out-of-stock" && badge === "hết hàng")
                ) {
                    item.style.display = "";
                } else {
                    item.style.display = "none";
                }
            });
        });
    }

    // Khởi tạo tất cả tooltip trong trang
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
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
        {id: 1, name: 'Văn học'},
        {id: 2, name: 'Kinh tế'},
        {id: 3, name: 'Tâm lý - Kỹ năng sống'},
        {id: 4, name: 'Thiếu nhi'},
        {id: 5, name: 'Tiểu sử - Hồi ký'},
        {id: 6, name: 'Giáo khoa - Tham khảo'},
        {id: 7, name: 'Ngoại ngữ'},
        {id: 8, name: 'Comics - Manga'}
    ];

    // Thêm các tùy chọn thể loại vào select
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.name;
        categorySelect.appendChild(option);
    });
});
