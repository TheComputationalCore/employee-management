/* ==========================================================
   MASTER UI SCRIPT – Employee Management System
   ========================================================== */

/* ================================
   PRELOADER
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    const preloader = document.getElementById("preloader");

    setTimeout(() => {
        preloader.style.opacity = "0";

        setTimeout(() => preloader.remove(), 450);
        document.body.classList.add("loaded");
    }, 300);
});

/* ================================
   DARK MODE TOGGLE
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    const themeBtn = document.getElementById("themeToggle");
    if (!themeBtn) return;

    // Apply saved theme
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

/* ================================
   SIDEBAR COLLAPSE (Mobile)
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".sidebar");
    const toggleBtn = document.querySelector(".sidebar-toggle");

    if (!sidebar || !toggleBtn) return;

    toggleBtn.addEventListener("click", () => {
        sidebar.classList.toggle("open");
    });
});

/* ================================
   SWEETALERT DELETE CONFIRMATION
   ================================ */
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
                confirmButtonColor: "#e63946",
                cancelButtonColor: "#6c757d",
                confirmButtonText: "Delete",
                background: "rgba(255,255,255,0.95)",
                backdrop: "rgba(0,0,0,0.4)"
            }).then(result => {
                if (result.isConfirmed) window.location.href = link.href;
            });
        });
    });
});

/* ================================
   BOOTSTRAP TOASTS API
   ================================ */
function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");
    const toast = document.createElement("div");

    toast.className = `toast align-items-center text-bg-${type} border-0 show`;
    toast.setAttribute("role", "alert");

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

/* ================================
   FORM VALIDATION
   ================================ */
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

/* ================================
   FLOATING LABEL "HAS VALUE"
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".floating-input").forEach(input => {
        if (input.value.trim() !== "") {
            input.classList.add("has-value");
        }

        input.addEventListener("input", () => {
            if (input.value.trim() !== "") input.classList.add("has-value");
            else input.classList.remove("has-value");
        });
    });
});

/* ================================
   AUTO-CAPITALIZE NAME FIELDS
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("#firstName, #lastName").forEach(input => {
        input.addEventListener("input", () => {
            if (!input.value.length) return;
            input.value = input.value.charAt(0).toUpperCase() + input.value.slice(1);
        });
    });
});

/* ================================
   PHONE NUMBER FORMAT
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        input.addEventListener("input", () => {
            let val = input.value.replace(/\D/g, "").slice(0, 10);
            if (val.length > 6) val = `${val.slice(0,3)}-${val.slice(3,6)}-${val.slice(6)}`;
            else if (val.length > 3) val = `${val.slice(0,3)}-${val.slice(3)}`;
            input.value = val;
        });
    });
});

/* ================================
   SALARY FORMAT ON BLUR
   ================================ */
document.addEventListener("DOMContentLoaded", () => {
    const salary = document.getElementById("salary");

    if (!salary) return;

    salary.addEventListener("blur", () => {
        const num = parseFloat(salary.value);
        if (!isNaN(num)) salary.value = num.toFixed(2);
    });
});
