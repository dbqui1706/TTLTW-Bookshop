// Sidebar Toggle
document
    .getElementById("sidebarCollapse")
    .addEventListener("click", function () {
        document.getElementById("sidebar").classList.toggle("collapsed");
        document.getElementById("content").classList.toggle("expanded");
    });

// Submenu Toggle
document.querySelectorAll(".dropdown-toggle").forEach((item) => {
    item.addEventListener("click", (event) => {
        event.preventDefault();
        const submenu = document.getElementById(
            item.getAttribute("data-bs-toggle")
        );
        submenu.classList.toggle("show");
    });
});

// Revenue Chart
const revenueCtx = document
    .getElementById("revenueChart")
    .getContext("2d");
const revenueChart = new Chart(revenueCtx, {
    type: "line",
    data: {
        labels: [
            "T1",
            "T2",
            "T3",
            "T4",
            "T5",
            "T6",
            "T7",
            "T8",
            "T9",
            "T10",
            "T11",
            "T12",
        ],
        datasets: [
            {
                label: "Doanh thu 2024",
                data: [
                    18500, 21000, 24000, 23000, 24500, 27000, 28500, 32000, 36500,
                    34000, 32500, 38000,
                ],
                borderColor: "#0d6efd",
                backgroundColor: "rgba(13, 110, 253, 0.1)",
                borderWidth: 2,
                fill: true,
                tension: 0.3,
            },
            {
                label: "Doanh thu 2023",
                data: [
                    15000, 16500, 18000, 17000, 19500, 21500, 22000, 24500, 26000,
                    25000, 24500, 26500,
                ],
                borderColor: "#6c757d",
                backgroundColor: "rgba(108, 117, 125, 0.1)",
                borderWidth: 2,
                fill: true,
                tension: 0.3,
            },
        ],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: "top",
            },
            tooltip: {
                mode: "index",
                intersect: false,
                callbacks: {
                    label: function (context) {
                        let label = context.dataset.label || "";
                        if (label) {
                            label += ": ";
                        }
                        if (context.parsed.y !== null) {
                            label += new Intl.NumberFormat("vi-VN", {
                                style: "currency",
                                currency: "VND",
                            }).format(context.parsed.y * 1000);
                        }
                        return label;
                    },
                },
            },
        },
        scales: {
            x: {
                grid: {
                    display: false,
                },
            },
            y: {
                beginAtZero: true,
                ticks: {
                    callback: function (value) {
                        return value.toLocaleString("vi-VN") + "K";
                    },
                },
            },
        },
    },
});

// Product Pie Chart
const pieCtx = document
    .getElementById("productPieChart")
    .getContext("2d");
const productPieChart = new Chart(pieCtx, {
    type: "doughnut",
    data: {
        labels: ["Điện thoại", "Laptop", "Máy tính bảng", "Phụ kiện", "Khác"],
        datasets: [
            {
                data: [35, 25, 15, 20, 5],
                backgroundColor: [
                    "#0d6efd",
                    "#20c997",
                    "#ffc107",
                    "#dc3545",
                    "#6c757d",
                ],
                borderWidth: 0,
            },
        ],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: "bottom",
            },
        },
    },
});

// API
const api = axios.create({
    baseURL: "http://localhost:8080/api/v1",
    headers: {
        "Content-Type": "application/json",
    },
});