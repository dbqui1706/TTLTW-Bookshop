document.addEventListener('DOMContentLoaded', function () {
    // Dữ liệu mẫu thể loại
    const categories = [
        { id: 1, name: 'Văn học' },
        { id: 2, name: 'Kinh tế' },
        { id: 3, name: 'Tâm lý - Kỹ năng sống' },
        { id: 4, name: 'Thiếu nhi' },
        { id: 5, name: 'Tiểu sử - Hồi ký' },
        { id: 6, name: 'Giáo khoa - Tham khảo' },
        { id: 7, name: 'Ngoại ngữ' },
        { id: 8, name: 'Comics - Manga' },
        { id: 9, name: 'Báo in' }
    ];

    // Biến để theo dõi xem editor đã được khởi tạo chưa
    let editEditorInitialized = false;
    let editEditor = null;

    // Nạp danh sách thể loại vào dropdown
    function loadCategories() {
        const categorySelect = document.getElementById('editProductCategory');
        if (categorySelect) {
            // Xóa tất cả options hiện tại trừ option mặc định
            while (categorySelect.options.length > 1) {
                categorySelect.remove(1);
            }

            // Thêm categories mới
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
    }

    // Khởi tạo Froala Editor cho chỉnh sửa
    function initEditFroalaEditor() {
        if (!editEditorInitialized) {
            const editorElement = document.getElementById('edit-froala-editor');
            if (editorElement) {
                try {
                    editEditor = new FroalaEditor('#edit-froala-editor', {
                        placeholderText: 'Viết mô tả sách...',
                        charCounterCount: true,
                        charCounterMax: 5000,
                        height: 150,
                        toolbarButtons: {
                            moreText: {
                                buttons: ['bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', 'fontFamily', 'fontSize', 'textColor', 'backgroundColor', 'clearFormatting'],
                                align: 'left',
                                buttonsVisible: 4
                            },
                            moreParagraph: {
                                buttons: ['alignLeft', 'alignCenter', 'alignRight', 'alignJustify', 'formatOL', 'formatUL', 'paragraphFormat', 'lineHeight', 'outdent', 'indent'],
                                align: 'left',
                                buttonsVisible: 3
                            },
                            moreRich: {
                                buttons: ['insertLink', 'insertImage', 'insertTable', 'emoticons', 'specialCharacters', 'insertHR'],
                                align: 'left',
                                buttonsVisible: 3
                            },
                            moreMisc: {
                                buttons: ['undo', 'redo', 'fullscreen', 'html'],
                                align: 'right',
                                buttonsVisible: 2
                            }
                        },
                        events: {
                            'contentChanged': function () {
                                // Cập nhật giá trị của textarea khi nội dung thay đổi
                                document.getElementById('editProductDescription').value = this.html.get();
                            }
                        },
                        attribution: false, // Ẩn "Powered by Froala"
                        language: 'vi'
                    });

                    editEditorInitialized = true;
                    console.log('Edit Froala Editor đã được khởi tạo thành công');
                } catch (error) {
                    console.error('Lỗi khi khởi tạo Edit Froala Editor:', error);
                }
            }
        }
    }

    // Xử lý nút "Xem" (eye icon)
    const viewButtons = document.querySelectorAll('.btn-outline-secondary');

    viewButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Lấy thông tin từ hàng được chọn
            const row = this.closest('tr');
            const id = row.cells[0].textContent;
            const image = row.cells[1].querySelector('img').src;
            const name = row.cells[2].textContent;
            const category = row.cells[3].querySelector('span').textContent;
            const price = row.cells[4].textContent;
            const originalPrice = row.cells[5].textContent;
            const quantity = row.cells[6].querySelector('span').textContent;
            const status = row.cells[7].querySelector('span').textContent;

            // Giả lập dữ liệu chi tiết (trong thực tế, bạn sẽ tải từ API)
            const productDetails = {
                id: id,
                name: name,
                category: category,
                price: price,
                originalPrice: originalPrice,
                quantity: quantity,
                status: status,
                author: 'Nguyễn Văn A',
                publisher: 'NXB Trẻ',
                yearPublishing: 2023,
                pages: 320,
                totalBuy: 58,
                description: '<p>Đây là mô tả chi tiết về sản phẩm. Bạn có thể thấy thông tin đầy đủ về cuốn sách, nội dung, tác giả và nhiều thông tin hữu ích khác.</p><p>Sách được xuất bản năm 2023 bởi NXB Trẻ, gồm 320 trang với nhiều hình ảnh minh họa và nội dung phong phú.</p>',
                discount: '10%',
                shop: 'Có',
                startsAt: '12/02/2025 00:00',
                endsAt: '12/05/2025 23:59'
            };

            // Điền thông tin vào modal xem chi tiết
            document.getElementById('viewProductId').textContent = productDetails.id;
            document.getElementById('viewProductName').textContent = productDetails.name;
            document.getElementById('viewProductCategory').textContent = productDetails.category;
            document.getElementById('viewProductPrice').textContent = productDetails.price;
            document.getElementById('viewProductOriginalPrice').textContent = productDetails.originalPrice;
            document.getElementById('viewProductQuantity').textContent = productDetails.quantity;
            document.getElementById('viewProductStatus').textContent = productDetails.status;
            document.getElementById('viewProductImage').src = image;
            document.getElementById('viewProductAuthor').textContent = productDetails.author;
            document.getElementById('viewProductPublisher').textContent = productDetails.publisher;
            document.getElementById('viewProductYear').textContent = productDetails.yearPublishing;
            document.getElementById('viewProductPages').textContent = productDetails.pages;
            document.getElementById('viewProductTotalBuy').textContent = productDetails.totalBuy;
            document.getElementById('viewProductDescription').innerHTML = productDetails.description;
            document.getElementById('viewProductDiscount').textContent = productDetails.discount;
            document.getElementById('viewProductShop').textContent = productDetails.shop;
            document.getElementById('viewProductStartsAt').textContent = productDetails.startsAt;
            document.getElementById('viewProductEndsAt').textContent = productDetails.endsAt;

            // Mở modal
            const viewModal = new bootstrap.Modal(document.getElementById('viewProductModal'));
            viewModal.show();
        });
    });

    // Xử lý nút chuyển từ xem sang chỉnh sửa
    const editFromViewBtn = document.getElementById('editFromViewBtn');

    if (editFromViewBtn) {
        editFromViewBtn.addEventListener('click', function () {
            // Đóng modal xem
            const viewModal = bootstrap.Modal.getInstance(document.getElementById('viewProductModal'));
            viewModal.hide();

            // Lấy ID sản phẩm từ modal xem
            const productId = document.getElementById('viewProductId').textContent;

            // Giả lập việc tải dữ liệu sản phẩm để chỉnh sửa (trong thực tế, sẽ tải từ API)
            // Ở đây chúng ta sử dụng lại dữ liệu từ modal xem
            const productData = {
                id: productId,
                name: document.getElementById('viewProductName').textContent,
                category: document.getElementById('viewProductCategory').textContent,
                price: document.getElementById('viewProductPrice').textContent.replace(/[^\d]/g, ''),
                discount: document.getElementById('viewProductDiscount').textContent.replace('%', ''),
                quantity: document.getElementById('viewProductQuantity').textContent,
                totalBuy: document.getElementById('viewProductTotalBuy').textContent,
                author: document.getElementById('viewProductAuthor').textContent,
                publisher: document.getElementById('viewProductPublisher').textContent,
                yearPublishing: document.getElementById('viewProductYear').textContent,
                pages: document.getElementById('viewProductPages').textContent,
                description: document.getElementById('viewProductDescription').innerHTML,
                imageSrc: document.getElementById('viewProductImage').src,
                shop: document.getElementById('viewProductShop').textContent === 'Có' ? '1' : '0',
                startsAt: document.getElementById('viewProductStartsAt').textContent,
                endsAt: document.getElementById('viewProductEndsAt').textContent
            };

            // Điền dữ liệu vào form chỉnh sửa
            document.getElementById('editProductId').value = productData.id;
            document.getElementById('editProductName').value = productData.name;
            document.getElementById('editProductPrice').value = productData.price;
            document.getElementById('editProductDiscount').value = productData.discount;
            document.getElementById('editProductQuantity').value = productData.quantity;
            document.getElementById('editProductTotalBuy').value = productData.totalBuy;
            document.getElementById('editProductAuthor').value = productData.author;
            document.getElementById('editProductPublisher').value = productData.publisher;
            document.getElementById('editProductYearPublishing').value = productData.yearPublishing;
            document.getElementById('editProductPages').value = productData.pages;
            document.getElementById('editImagePreview').querySelector('img').src = productData.imageSrc;

            // Xử lý radio button shop
            if (productData.shop === '1') {
                document.getElementById('editProductShopYes').checked = true;
            } else {
                document.getElementById('editProductShopNo').checked = true;
            }

            // Tải danh sách thể loại và chọn thể loại phù hợp
            loadCategories();

            // Tìm category ID dựa trên tên
            const categoryId = categories.find(c => c.name === productData.category)?.id || '';
            if (categoryId) {
                document.getElementById('editProductCategory').value = categoryId;
            }

            // Khởi tạo editor và đặt nội dung
            setTimeout(() => {
                initEditFroalaEditor();
                if (editEditor) {
                    editEditor.html.set(productData.description);
                }
            }, 300);

            // Convert date strings về định dạng datetime-local
            // Trong thực tế, bạn cần xử lý chuyển đổi định dạng ngày tháng phù hợp
            // Ở đây chúng ta bỏ qua để đơn giản

            // Mở modal chỉnh sửa
            const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
            editModal.show();
        });
    }

    // Xử lý nút "Chỉnh sửa" (pencil icon)
    const editButtons = document.querySelectorAll('.btn-outline-primary');

    editButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Lấy thông tin từ hàng được chọn
            const row = this.closest('tr');
            const id = row.cells[0].textContent;
            const image = row.cells[1].querySelector('img').src;
            const name = row.cells[2].textContent;
            const category = row.cells[3].querySelector('span').textContent;
            const price = row.cells[4].textContent.replace(/[^\d]/g, '');
            const quantity = row.cells[6].querySelector('span').textContent;

            // Giả lập dữ liệu chi tiết (trong thực tế, bạn sẽ tải từ API)
            const productData = {
                id: id,
                name: name,
                category: category,
                price: price,
                discount: "10",
                quantity: quantity,
                totalBuy: "58",
                author: 'Nguyễn Văn A',
                publisher: 'NXB Trẻ',
                yearPublishing: "2023",
                pages: "320",
                description: '<p>Đây là mô tả chi tiết về sản phẩm. Bạn có thể thấy thông tin đầy đủ về cuốn sách, nội dung, tác giả và nhiều thông tin hữu ích khác.</p><p>Sách được xuất bản năm 2023 bởi NXB Trẻ, gồm 320 trang với nhiều hình ảnh minh họa và nội dung phong phú.</p>',
                imageSrc: image,
                shop: "1",
                startsAt: "",
                endsAt: ""
            };

            // Điền dữ liệu vào form chỉnh sửa
            document.getElementById('editProductId').value = productData.id;
            document.getElementById('editProductName').value = productData.name;
            document.getElementById('editProductPrice').value = productData.price;
            document.getElementById('editProductDiscount').value = productData.discount;
            document.getElementById('editProductQuantity').value = productData.quantity;
            document.getElementById('editProductTotalBuy').value = productData.totalBuy;
            document.getElementById('editProductAuthor').value = productData.author;
            document.getElementById('editProductPublisher').value = productData.publisher;
            document.getElementById('editProductYearPublishing').value = productData.yearPublishing;
            document.getElementById('editProductPages').value = productData.pages;
            document.getElementById('editImagePreview').querySelector('img').src = productData.imageSrc;

            // Xử lý radio button shop
            if (productData.shop === '1') {
                document.getElementById('editProductShopYes').checked = true;
            } else {
                document.getElementById('editProductShopNo').checked = true;
            }

            // Tải danh sách thể loại và chọn thể loại phù hợp
            loadCategories();

            // Tìm category ID dựa trên tên
            const categoryId = categories.find(c => c.name === productData.category)?.id || '';
            if (categoryId) {
                document.getElementById('editProductCategory').value = categoryId;
            }

            // Khởi tạo editor và đặt nội dung
            setTimeout(() => {
                initEditFroalaEditor();
                if (editEditor) {
                    editEditor.html.set(productData.description);
                }
            }, 300);

            // Mở modal chỉnh sửa
            const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
            editModal.show();
        });
    });

    // Xử lý nút "Xóa" (trash icon)
    const deleteButtons = document.querySelectorAll('.btn-outline-danger');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Lấy thông tin từ hàng được chọn
            const row = this.closest('tr');
            const id = row.cells[0].textContent;
            const name = row.cells[2].textContent;

            // Điền thông tin vào modal xác nhận xóa
            document.getElementById('deleteProductName').textContent = name;

            // Mở modal xác nhận xóa
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteProductModal'));
            deleteModal.show();

            // Gán ID sản phẩm cho nút xác nhận xóa
            document.getElementById('confirmDeleteBtn').setAttribute('data-product-id', id);
        });
    });

    // Xử lý nút xác nhận xóa
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', function () {
            const productId = this.getAttribute('data-product-id');

            // Giả lập việc xóa sản phẩm (trong thực tế, bạn sẽ gọi API)
            console.log('Đang xóa sản phẩm có ID:', productId);

            // Đóng modal
            const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteProductModal'));
            deleteModal.hide();

            // Hiển thị thông báo thành công
            alert('Đã xóa sản phẩm thành công!');

            // Trong thực tế, bạn sẽ cập nhật UI sau khi xóa thành công
            // Ví dụ: Xóa hàng khỏi bảng
            const rows = document.querySelectorAll('tbody tr');
            rows.forEach(row => {
                if (row.cells[0].textContent === productId) {
                    row.remove();
                }
            });
        });
    }

    // Xử lý nút cập nhật sản phẩm
    const updateProductBtn = document.getElementById('updateProductBtn');

    if (updateProductBtn) {
        updateProductBtn.addEventListener('click', function () {
            // Cập nhật nội dung từ Froala Editor vào textarea
            if (editEditor) {
                document.getElementById('editProductDescription').value = editEditor.html.get();
            }

            const editProductForm = document.getElementById('editProductForm');

            if (editProductForm.checkValidity()) {
                // Giả lập việc cập nhật sản phẩm (trong thực tế, bạn sẽ gọi API)
                console.log('Đang cập nhật sản phẩm...');

                // Đóng modal
                const editModal = bootstrap.Modal.getInstance(document.getElementById('editProductModal'));
                editModal.hide();

                // Hiển thị thông báo thành công
                alert('Đã cập nhật sản phẩm thành công!');

                // Trong thực tế, bạn sẽ cập nhật UI sau khi cập nhật thành công
                // Ví dụ: Cập nhật thông tin trong hàng
                const productId = document.getElementById('editProductId').value;
                const productName = document.getElementById('editProductName').value;
                const categorySelect = document.getElementById('editProductCategory');
                const categoryName = categorySelect.options[categorySelect.selectedIndex].text;
                const productPrice = document.getElementById('editProductPrice').value;
                const productQuantity = document.getElementById('editProductQuantity').value;

                const rows = document.querySelectorAll('tbody tr');
                rows.forEach(row => {
                    if (row.cells[0].textContent === productId) {
                        row.cells[2].textContent = productName;
                        row.cells[3].querySelector('span').textContent = categoryName;
                        row.cells[4].textContent = new Intl.NumberFormat('vi-VN').format(productPrice) + 'đ';
                        row.cells[6].querySelector('span').textContent = productQuantity;
                    }
                });
            } else {
                editProductForm.reportValidity();
            }
        });
    }

    // Xử lý xem trước hình ảnh khi chỉnh sửa
    const editProductImage = document.getElementById('editProductImage');
    const editImagePreview = document.getElementById('editImagePreview');

    if (editProductImage && editImagePreview) {
        const previewImage = editImagePreview.querySelector('img');

        editProductImage.addEventListener('change', function () {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                };
                reader.readAsDataURL(this.files[0]);
            }
        });
    }
});