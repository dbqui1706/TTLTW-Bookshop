const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");
const contextPathMetaTag = document.querySelector("meta[name='contextPath']");

const citis = document.getElementById("city");
const districts = document.getElementById("district");
const wards = document.getElementById("ward");

// Get references to the input fields
const fullNameInput = document.getElementById("full-name-input");
const emailInput = document.getElementById("email-input");
const addressInput = document.getElementById("address");
const phoneNumberInput = document.getElementById("phone");

const cardProducts = document.querySelector("#id-card-products");
const deliveryMethodRadioElements = [...document.querySelectorAll("input[name='delivery']")];
const showDetailButton = document.getElementById("show-detail");
const completeOrderButton = document.getElementById("btn-complete-order");

const buyNowBtn = document.querySelector("#buy-now");


const orderForm = document.getElementById("id-order-form");
const Parameter = {
    url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
    method: "GET",
    responseType: "json", // Correct value for axios
};

axios(Parameter)
    .then(result => {
        if (Array.isArray(result.data)) {
            renderCity(result.data);
        } else {
            console.error("Data is not an array:", result.data);
        }
    })
    .catch(function (error) {
        console.error("API request failed:", error);
    });

function renderCity(data) {
    data.sort((a, b) => a.Name.localeCompare(b.Name));

    for (const x of data) {
        citis.options[citis.options.length] = new Option(x.Name, x.Id);
    }

    citis.onchange = function () {
        districts.length = 1; // Clear previous options
        wards.length = 1;     // Clear previous options
        if (this.value !== "") {
            const result = data.filter(n => n.Id === this.value);

            for (const k of result[0].Districts) {
                districts.options[districts.options.length] = new Option(k.Name, k.Id);
            }
        }
    };

    districts.onchange = function () {
        wards.length = 1; // Clear previous options
        const dataCity = data.filter((n) => n.Id === citis.value);
        if (this.value !== "") {
            const dataWards = dataCity[0].Districts.filter(n => n.Id === this.value)[0].Wards;

            for (const w of dataWards) {
                wards.options[wards.options.length] = new Option(w.Name, w.Id);
            }
        }
    };
}


async function _fetchOrderInfo() {
    // Thêm UTF-8 để tránh lỗi khi gửi dữ liệu có dấu
    const response = await fetch(`${contextPathMetaTag.content}/order-fetch-data`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "Accept-Charset": "utf-8",
        },
    });
    return [response.status, await response.json()];
}

// Get data orderInfo from API '/order-fetch-data'
const state = {
    orderInfo: null,
    products: [],

    // Khởi tạo state và sự kiện
    initState: async () => {
        const [status, data] = await _fetchOrderInfo();
        if (status === 200) {
            state.orderInfo = data;
            state.products = data.products;
            renderOrderInfo();
        }

        // Thiết lập sự kiện
        state.setupEventListeners();
    },

    // Thiết lập các sự kiện
    setupEventListeners: () => {
        // 1. Sự kiện chọn phương thức vận chuyển
        deliveryMethodRadioElements.forEach(element => {
            element.style.cursor = "pointer";
            element.addEventListener("click", updateDeliveryMethod);
        });

        // 2. Sự kiện hoàn tất đơn hàng
        completeOrderButton.addEventListener("click", event => {
            event.preventDefault();
            state.submitOrder();
        });

        // 3. Ẩn hiện thông tin sản phẩm
        showDetailButton.addEventListener("click", () => {
            showDetailButton.style.cursor = "pointer";
            cardProducts.style.display =
                cardProducts.style.display === "none" || cardProducts.style.display === ""
                    ? "block"
                    : "none";
        });
    },

    // Chọn private key
    selectPrivateKey: () =>
        new Promise(resolve => {
            const input = document.createElement("input");
            input.type = "file";
            input.accept = ".pem";
            input.addEventListener("change", event => resolve(event.target.files[0]));
            input.click();
        }),
    // Kiểm tra xem người dùng có public key nào được lưu trên server với status = True không
    isHavePublicKey: async () => {
        const response = await fetch(`${contextPathMetaTag.content}/keys?checkKey=true`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Accept-Charset": "utf-8",
            },
        })
        return [response.status, await response.json()];
    },
    dataToSign: () => {
        return {
            userId: currentUserIdMetaTag.content,
            receiver: state.orderInfo.fullName,
            emailReceiver: state.orderInfo.email,
            addressReceiver: state.orderInfo.address,
            phone: state.orderInfo.phone,
            city: state.orderInfo.city,
            district: state.orderInfo.district,
            ward: state.orderInfo.ward,
            totalPrice: parseInt(state.orderInfo.totalPrice),
            order: {
                deliveryMethod: state.orderInfo.deliveryMethod,
                deliveryPrice: state.orderInfo.deliveryFee,
                orderItems: state.products
                    .slice()
                    .sort((a, b) => a.productId - b.productId)
                    .map(product => ({
                        productId: product.productId,
                        price: parseInt(product.price),
                        discount: product.discount,
                        quantity: product.quantity,
                    })),
            },
        };
    },
    // Submit order
    submitOrder: async () => {
        if (!validateForm()) return;

        // Cập nhật thông tin từ form
        state.updateOrderInfo();

        // Lấy những thông tin cần thiết từ người dùng
        const dataPrepare = state.dataToSign();
        const jsonData = JSON.stringify(dataPrepare)

        // Yêu cầu người dùng nhập private key
        // Trước khi import private key thì cần kiểm tra xem người dùng đang có public key
        // nào được lưu trên server với status = 1 không ?,
        // nếu không thì cho người dùng lựa chọn
        // 1. Chuyyển người dùng đến trang tạo key "localhost:8080/keys"
        // 2. Nguười dùng tiêến hành order mà không cần ký
        // Kiểm tra public key trên server
        const [status, data] = await state.isHavePublicKey();
        const importKey = confirm("Bạn có muốn nhập private key để xác thực đơn hàng?");

        // Nếu người dùng chọn import private key
        if (importKey) {
            // Không có public key trên server
            if (status !== 200) {
                const createKey = confirm(
                    "Bạn chưa có public key nào được lưu trên server. Bạn có muốn tạo key mới không hay tiếp tục order mà không cần ký?"
                );
                // Chuyển hướng người dùng đến trang tạo key
                if (createKey) {
                    window.location.href = `${contextPathMetaTag.content}/keys`;
                    return;
                }
            } else {
                // Có public key, xử lý nhập private key
                const signatureData = await state.handlePrivateKeyImport(dataPrepare);
                if (!signatureData) return; // Người dùng không nhập key, dừng tiến trình
                dataPrepare.signature = signatureData;
            }
        }

        // Gửi đơn hàng
        dataPrepare.cartId = state.orderInfo.cartId;
        dataPrepare.jsonData = jsonData;
        console.log("Data to send:", dataPrepare);
        console.log("JSON String data: ", JSON.stringify(dataPrepare))
        await state.sendOrder(dataPrepare);
    },

    // Cập nhật thông tin order từ form
    updateOrderInfo: () => {
        state.orderInfo = {
            ...state.orderInfo,
            fullName: fullNameInput.value.trim(),
            email: emailInput.value.trim(),
            address: addressInput.value.trim(),
            phone: phoneNumberInput.value.trim(),
            city: citis.options[citis.selectedIndex].text,
            district: districts.options[districts.selectedIndex].text,
            ward: wards.options[wards.selectedIndex].text,
            deliveryMethod: document.querySelector("input[name='delivery']:checked").value,
            deliveryFee: deliveryMethodInputValues[document.querySelector("input[name='delivery']:checked").value].deliveryPrice,
        };
        console.log("Order info updated:", state.orderInfo);
    },

    // Xử lý nhập private key
    handlePrivateKeyImport: async (data) => {
        try {
            const privateKeyFile = await state.selectPrivateKey();
            if (!privateKeyFile) {
                alert("Bạn chưa chọn file private key.");
                return false;
            }
            const privateKeyContent = await privateKeyFile.text();
            console.log("Private key imported:\n", privateKeyContent);

            // TODO: Ký dữ liệu tại đây nếu cần
            // Thực hiện ký dữ liệu
            return await state.signData(privateKeyContent, data);
        } catch (error) {
            alert("Không thể đọc file private key. Vui lòng thử lại.");
            console.error("Error importing private key:", error);
            return false;
        }
    },
    signData: async (privateKeyPem, data) => {
        // Thực hiện ký dữ liệu
        // privateKey: Private key dạng PEM
        // Type format: PKCS#8
        // Thuật toán: RSA
        // Hash: SHA-256 with RSA
        // Dữ liệu cần ký: JSON.stringify(state.orderInfo)
        // fetch the part of the PEM string between header and footer
        try {
            // Loại bỏ header và footer khỏi PEM
            const pemHeader = "-----BEGIN PRIVATE KEY-----";
            const pemFooter = "-----END PRIVATE KEY-----";
            const pemContents = privateKeyPem
                .replace(pemHeader, "")
                .replace(pemFooter, "")
                .replace(/\s/g, ""); // Loại bỏ khoảng trắng và xuống dòng

            // Base64 decode chuỗi PEM
            const binaryDer = Uint8Array.from(atob(pemContents), c => c.charCodeAt(0));

            // Import private key
            const privateKey = await window.crypto.subtle.importKey(
                "pkcs8",
                binaryDer.buffer,
                {name: "RSASSA-PKCS1-v1_5", hash: "SHA-256"},
                false,
                ["sign"]
            );

            // Mã hóa dữ liệu cần ký
            const dataToSign = new TextEncoder().encode(JSON.stringify(data));

            // Thực hiện ký
            const signature = await window.crypto.subtle.sign(
                {name: "RSASSA-PKCS1-v1_5"},
                privateKey,
                dataToSign
            );

            // Chuyển chữ ký sang dạng base64
            return btoa(String.fromCharCode(...new Uint8Array(signature)));
        } catch (error) {
            console.error("Lỗi khi ký dữ liệu:", error);
            throw error;
        }
    },

    // Gửi đơn hàng lên server
    sendOrder: async (data) => {
        try {
            const response = await fetch(`${contextPathMetaTag.content}/order-submit`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "Accept-Charset": "utf-8",
                },
                body: JSON.stringify(data),
            });
            const [status, text] = [response.status, await response.json()];

            if (status === 200) {
                await showSwal({
                    icon: "success",
                    title: text
                });
                // Xử lý sau khi hiển thị thông báo
                // window.location.href = `${contextPathMetaTag.content}/`;

                history.replaceState(null, "", `${contextPathMetaTag.content}/`);
                window.location.href = `${contextPathMetaTag.content}/`;
                return;
            }

            if (status === 403) {
                await showSwal({
                    icon: "error",
                    title: "Invalid signature",
                    text: "Bạn đã nhập sai private key. Vui lòng thử lại."
                });
                // Xử lý sau khi hiển thị thông báo
                history.replaceState(null, "", `${contextPathMetaTag.content}/`);
                window.location.href = `${contextPathMetaTag.content}/`;
                return;
            }

            // Mặc định cho các lỗi khác
            await showSwal({
                icon: "error",
                title: "Oops...",
                text: "Có lỗi xảy ra. Vui lòng thử lại."
            });
            history.replaceState(null, "", `${contextPathMetaTag.content}/`);
            window.location.href = `${contextPathMetaTag.content}/`;
            // Xử lý sau khi hiển thị thông báo
            // window.location.href = `${contextPathMetaTag.content}/`;
            console.error("Lỗi khi gửi đơn hàng:", text);
        } catch (error) {
            alert("Có lỗi xảy ra. Vui lòng thử lại.");
            console.error("Lỗi khi gửi đơn hàng:", error);
        }
    },

};


function renderOrderInfo() {
    // Set values in input fields
    fullNameInput.value = state.orderInfo.fullName || "";
    emailInput.value = state.orderInfo.email || "";
    addressInput.value = state.orderInfo.address || "";
    phoneNumberInput.value = state.orderInfo.phone || "";

    // Render products
    cardProducts.innerHTML = state.products.map(renderProductRow).join("");

    // Render order summary
    renderOrderSummary();
}

function renderProductRow(product) {
    return `
        <div class="d-flex align-items-center mb-2">
            <img
                alt="${product.name}" class="me-3"
                src="${contextPathMetaTag.content}/image/${product.image}"
                width="60" height="70"
            >
            <div>
                <h6 class="mb-1">Tên sách: ${product.name}</h6>
                <p class="small mb-1">Số lượng: ${product.quantity}</p>
                <p class="small mb-1">Giá: ${_formatPrice(product.price * product.quantity)}₫</p>
            </div>
        </div>
    `;
}

const deliveryMethodInputValues = {
    "1": {
        deliveryMethod: 1,
        deliveryPrice: 15000,
    },
    "2": {
        deliveryMethod: 2,
        deliveryPrice: 50000,
    },
};

function renderOrderSummary() {

    const subtotal = state.products.reduce((sum, product) => sum + product.price * product.quantity, 0);
    const deliveryMethod = state.orderInfo.deliveryMethod;
    const deliveryPrice = state.orderInfo.deliveryFee;

    const total = subtotal + deliveryPrice;

    const summaryHTML = `
        <div class="d-flex justify-content-between mb-2">
            <span>Tạm tính</span>
            <span>${_formatPrice(subtotal)}₫</span>
        </div>
        <div class="d-flex justify-content-between mb-3">
            <span>Phí vận chuyển</span>
            <span>+ ${_formatPrice(deliveryPrice)}₫</span>
        </div>
        <div class="d-flex justify-content-between fw-bold">
            <span>TỔNG CỘNG</span>
            <span>${_formatPrice(total)}₫</span>
        </div>
    `;
    state.orderInfo.totalPrice = total;
    document.querySelector("#summary").innerHTML = summaryHTML;
}

// Xác thực form
function validateForm() {
    let isValid = true;
    const errorMessages = [];

    const fullName = fullNameInput.value.trim();
    const email = emailInput.value.trim();
    const address = addressInput.value.trim();
    const phone = phoneNumberInput.value.trim();
    const city = citis.value;
    const district = districts.value;
    const ward = wards.value;
    const deliveryMethod = document.querySelector("input[name='delivery']:checked");

    // Kiểm tra tên đầy đủ
    if (!fullName) {
        errorMessages.push("Họ và tên không được để trống.");
        isValid = false;
    }

    // Kiểm tra email
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailPattern.test(email)) {
        errorMessages.push("Email không hợp lệ.");
        isValid = false;
    }

    // Kiểm tra địa chỉ
    if (!address) {
        errorMessages.push("Địa chỉ không được để trống.");
        isValid = false;
    }

    // Kiểm tra số điện thoại
    const phonePattern = /^[0-9]{10,11}$/;
    if (!phone || !phonePattern.test(phone)) {
        errorMessages.push("Số điện thoại không hợp lệ.");
        isValid = false;
    }

    // Kiểm tra thành phố, quận, phường
    if (!city || city === "Chọn Tỉnh/TP") {
        errorMessages.push("Vui lòng chọn thành phố.");
        isValid = false;
    }
    if (!district || district === "Chọn Quận/Huyện") {
        errorMessages.push("Vui lòng chọn quận.");
        isValid = false;
    }
    if (!ward || ward === "Phường/Xã") {
        errorMessages.push("Vui lòng chọn phường.");
        isValid = false;
    }

    // Hiển thị lỗi nếu có
    if (!isValid) {
        alert("Vui lòng kiểm tra lại thông tin:\n" + errorMessages.join("\n"));
    }
    return isValid;
}


// Hàm update khi chọn phuương thức vận chuyển
function updateDeliveryMethod(event) {
    console.log("Radio click", event.target.value); // Debugging log
    const deliveryMethod = deliveryMethodInputValues[event.target.value];
    if (deliveryMethod) {
        state.orderInfo.deliveryMethod = deliveryMethod.deliveryMethod;
        state.orderInfo.deliveryFee = deliveryMethod.deliveryPrice;
        renderOrderSummary();
    } else {
        console.error("Invalid delivery method selected:", event.target.value);
    }
}

function _formatPrice(price) {
    return new Intl.NumberFormat('vi-VN').format(price.toFixed());
}

function showSwal({icon, title, text, timer = 1500, position = "center"}) {
    return Swal.fire({
        position,
        icon,
        title,
        text,
        showConfirmButton: false,
        timer
    });
}

if (currentUserIdMetaTag) {
    state.initState();
}