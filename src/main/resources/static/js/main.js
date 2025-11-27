/* ==========================================================
   PREMIUM UI SCRIPT – Employee Management System
   Includes:
   ✔ Preloader
   ✔ Dark mode toggle
   ✔ SweetAlert delete
   ✔ Floating input behavior
   ✔ Phone formatter
   ✔ Salary formatter
   ✔ Sidebar collapse
   ✔ Form validation
   ✔ Smooth search filter UX
   ✔ Sorting highlight animation
   ✔ Pagination auto-scroll
   ========================================================== */


/* ===================== PRELOADER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    setTimeout(() => {
        const preloader = document.getElementById("preloader");
        preloader.style.opacity = "0";
        setTimeout(() => preloader?.remove(), 400);
        document.body.classList.add("loaded");
    }, 250);
});


/* ===================== DARK MODE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("themeToggle");
    if (!toggle) return;

    // Init saved theme
    if (localStorage.getItem("theme") === "dark") {
        document.documentElement.classList.add("dark-mode");
    }

    toggle.addEventListener("click", () => {
        document.documentElement.classList.toggle("dark-mode");
        localStorage.setItem(
            "theme",
            document.documentElement.classList.contains("dark-mode") ? "dark" : "light"
        );
    });
});


/* ===================== SWEETALERT DELETE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const links = document.querySelectorAll("a.delete-confirm");

    links.forEach(link => {
        link.addEventListener("click", event => {
            event.preventDefault();
            Swal.fire({
                title: "Delete Employee?",
                text: "This action cannot be undone.",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#e74c3c",
                cancelButtonColor: "#7f8c8d",
                confirmButtonText: "Delete"
            }).then(result => {
                if (result.isConfirmed) window.location.href = link.href;
            });
        });
    });
});


/* ===================== FLOATING INPUT TRIGGER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".floating-input").forEach(input => {
        if (input.value.trim() !== "") input.classList.add("has-value");

        input.addEventListener("input", () => {
            if (input.value.trim() !== "") input.classList.add("has-value");
            else input.classList.remove("has-value");
        });
    });
});


/* ===================== PHONE FORMATTER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        input.addEventListener("input", () => {
            let v = input.value.replace(/\D/g, "").substring(0, 10);
            if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3");
            else if (v.length > 3) v = v.replace(/(\d{3})(\d+)/, "$1-$2");
            input.value = v;
        });
    });
});


/* ===================== SALARY FORMATTER ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const salary = document.getElementById("salary");
    if (salary) {
        salary.addEventListener("blur", () => {
            const val = parseFloat(salary.value);
            if (!isNaN(val)) salary.value = val.toFixed(2);
        });
    }
});


/* ===================== SIDEBAR COLLAPSE ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".sidebar");
    const toggle = document.querySelector(".sidebar-toggle");
    if (toggle && sidebar) {
        toggle.addEventListener("click", () => {
            sidebar.classList.toggle("open");
        });
    }
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


/* ===================== SORTING ANIMATION ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const sortLinks = document.querySelectorAll(".table-sort-link");
    sortLinks.forEach(link => {
        link.addEventListener("click", () => {
            link.classList.add("sort-clicked");
            setTimeout(() => link.classList.remove("sort-clicked"), 350);
        });
    });
});


/* ===================== PAGINATION AUTO-SCROLL TO TOP ===================== */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".pagination a").forEach(a => {
        a.addEventListener("click", () => {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    });
});


/* ===================== SEARCH INPUT DELAY ===================== */
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("searchInput");
    if (!input) return;

    let timer;
    input.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(() => input.closest("form").submit(), 350);
    });
});
