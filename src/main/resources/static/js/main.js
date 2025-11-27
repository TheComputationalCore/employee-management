/* ==========================================================
   MASTER UI SCRIPT – Employee Management System
   ========================================================== */

/* ===================== PRELOADER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    setTimeout(() => {
        const preloader = document.getElementById("preloader");
        preloader.style.opacity = "0";
        setTimeout(() => preloader.remove(), 400);

        document.body.classList.add("loaded");
    }, 300);
});

/* ===================== DARK MODE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const themeBtn = document.getElementById("themeToggle");

    if (!themeBtn) return;

    // Initialize theme
    if (localStorage.getItem("theme") === "dark") {
        document.documentElement.classList.add("dark-mode");
    }

    themeBtn.addEventListener("click", () => {
        document.documentElement.classList.toggle("dark-mode");

        const theme =
            document.documentElement.classList.contains("dark-mode")
                ? "dark"
                : "light";

        localStorage.setItem("theme", theme);
    });
});

/* ===================== SWEETALERT DELETE ===================== */
document.addEventListener("DOMContentLoaded", function () {
    const deleteLinks = document.querySelectorAll('a[href*="/delete/"]');

    deleteLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();

            Swal.fire({
                title: "Delete Employee?",
                text: "This action cannot be undone.",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#e74c3c",
                cancelButtonColor: "#7f8c8d",
                confirmButtonText: "Delete",
                background: "#ffffffdd",
                backdrop: "rgba(0,0,0,0.4)"
            }).then(result => {
                if (result.isConfirmed) window.location.href = link.href;
            });
        });
    });
});

/* ===================== TOAST NOTIFICATIONS ===================== */
function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");

    const toast = document.createElement("div");
    toast.className = `toast align-items-center text-bg-${type} border-0 fade show`;
    toast.role = "alert";
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    container.appendChild(toast);

    setTimeout(() => toast.remove(), 3500);
}

window.showToast = showToast;

/* ===================== AUTO-CAPITALIZE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("#firstName, #lastName").forEach(input => {
        input.addEventListener("input", () => {
            if (input.value.length > 0) {
                input.value =
                    input.value.charAt(0).toUpperCase() + input.value.slice(1);
            }
        });
    });
});

/* ===================== PHONE NUMBER FORMAT ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        input.addEventListener("input", () => {
            let val = input.value.replace(/\D/g, "").slice(0, 10);
            if (val.length > 6) val = val.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3");
            else if (val.length > 3) val = val.replace(/(\d{3})(\d+)/, "$1-$2");
            input.value = val;
        });
    });
});

/* ===================== SALARY BLUR FORMAT ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const salary = document.getElementById("salary");
    if (!salary) return;

    salary.addEventListener("blur", () => {
        const num = parseFloat(salary.value);
        if (!isNaN(num)) salary.value = num.toFixed(2);
    });
});

/* ===================== SIDEBAR COLLAPSE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".sidebar");
    const toggle = document.querySelector(".sidebar-toggle");

    if (!toggle || !sidebar) return;

    toggle.addEventListener("click", () => sidebar.classList.toggle("open"));
});

/* ===================== FORM VALIDATION ===================== */
(() => {
    const forms = document.querySelectorAll(".needs-validation");

    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add("was-validated");
        });
    });
})();
