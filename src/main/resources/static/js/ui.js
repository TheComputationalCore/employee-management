/* ==========================================================
   PREMIUM UI SCRIPT — Employee Management System
   ========================================================== */

/* ===================== PRELOADER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const preloader = document.getElementById("preloader");
    setTimeout(() => {
        preloader.style.opacity = "0";
        setTimeout(() => preloader.remove(), 400);
        document.body.classList.add("loaded");
    }, 200);
});


/* ===================== DARK MODE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const themeBtn = document.getElementById("themeToggle");
    if (!themeBtn) return;

    const root = document.documentElement;

    if (localStorage.getItem("theme") === "dark") {
        root.classList.add("dark-mode");
    }

    themeBtn.addEventListener("click", () => {
        root.classList.toggle("dark-mode");
        const active = root.classList.contains("dark-mode") ? "dark" : "light";
        localStorage.setItem("theme", active);
    });
});


/* ===================== SIDEBAR TOGGLE (MOBILE) ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".sidebar");
    const toggle = document.querySelector(".sidebar-toggle");

    if (!toggle || !sidebar) return;

    toggle.addEventListener("click", () => {
        sidebar.classList.toggle("open");
    });
});


/* ===================== SWEETALERT DELETE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const deleteLinks = document.querySelectorAll('a[href*="/delete/"]');

    deleteLinks.forEach(link => {
        link.addEventListener("click", e => {
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
window.showToast = function (message, type = "success") {
    const container = document.getElementById("toastContainer");
    if (!container) return;

    const toast = document.createElement("div");
    toast.className = `toast align-items-center text-bg-${type} border-0 fade show`;
    toast.role = "alert";

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
};


/* ===================== AUTO-CAPITALIZE NAMES ===================== */
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


/* ===================== PHONE FORMAT ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        input.addEventListener("input", () => {
            let v = input.value.replace(/\D/g, "").slice(0, 10);

            if (v.length > 6) {
                v = v.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3");
            } else if (v.length > 3) {
                v = v.replace(/(\d{3})(\d+)/, "$1-$2");
            }

            input.value = v;
        });
    });
});


/* ===================== SALARY FORMAT ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const salary = document.getElementById("salary");
    if (!salary) return;

    salary.addEventListener("blur", () => {
        const num = parseFloat(salary.value);
        if (!isNaN(num)) salary.value = num.toFixed(2);
    });
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


/* ==========================================================
   LIVE PREVIEW ENGINE (Split UI)
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
    const previewFields = document.querySelectorAll(".preview-field");

    if (previewFields.length === 0) return; // No preview on dashboard

    function updatePreview() {
        const fname = document.getElementById("preview_firstName")?.value || "";
        const lname = document.getElementById("preview_lastName")?.value || "";

        // Name
        document.getElementById("card_name").textContent =
            (fname + " " + lname).trim() || "Employee Name";

        // Email
        document.getElementById("card_email").textContent =
            document.getElementById("preview_email")?.value || "email@example.com";

        // Phone
        document.getElementById("card_phone").textContent =
            document.getElementById("preview_phone")?.value || "000-000-0000";

        // Department
        document.getElementById("card_department").textContent =
            document.getElementById("preview_department")?.value || "Department";

        // Position
        document.getElementById("card_position").textContent =
            document.getElementById("preview_position")?.value || "Position";

        // Salary
        const salary = document.getElementById("preview_salary")?.value || "0.00";
        document.getElementById("card_salary").textContent = "$" + salary;
    }

    previewFields.forEach(input => input.addEventListener("input", updatePreview));

    updatePreview(); // Initial
});
