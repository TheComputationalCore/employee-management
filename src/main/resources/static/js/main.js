/* =======================================================
   PREMIUM UI JAVASCRIPT FOR EMPLOYEE MANAGEMENT SYSTEM
   ======================================================= */

// -----------------------------------------
// SWEETALERT2 DELETE CONFIRMATION
// -----------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const deleteLinks = document.querySelectorAll('a[href*="/delete/"]');

    deleteLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();

            Swal.fire({
                title: "Delete Employee?",
                text: "This action cannot be undone.",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#d33",
                cancelButtonColor: "#3085d6",
                confirmButtonText: "Yes, delete",
                background: "#ffffffee",
                backdrop: "rgba(0,0,0,0.4)",
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = link.href;
                }
            });
        });
    });
});


// -----------------------------------------
// REAL-TIME TABLE SEARCH
// -----------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#employeeTable tr");

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const searchTerm = searchInput.value.toLowerCase();

            tableRows.forEach(row => {
                const rowText = row.innerText.toLowerCase();
                row.style.display = rowText.includes(searchTerm) ? "" : "none";
            });
        });
    }
});


// -----------------------------------------
// FLOATING LABELS SMOOTH BEHAVIOR
// -----------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const inputs = document.querySelectorAll(".floating-input");

    inputs.forEach(input => {
        // Trigger animation on load if field has value
        if (input.value.trim().length > 0) {
            input.classList.add("has-value");
        }

        // Trigger animation on input
        input.addEventListener("input", () => {
            if (input.value.trim().length > 0) {
                input.classList.add("has-value");
            } else {
                input.classList.remove("has-value");
            }
        });
    });
});


// -----------------------------------------
// AUTO-CAPITALIZE NAME FIELDS
// -----------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    const nameInputs = document.querySelectorAll("#firstName, #lastName");

    nameInputs.forEach(input => {
        input.addEventListener("input", function () {
            let value = input.value;
            input.value = value.charAt(0).toUpperCase() + value.slice(1);
        });
    });
});


// -----------------------------------------
// PHONE NUMBER FORMATTER (123-456-7890)
// -----------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    const phoneFields = document.querySelectorAll('input[type="tel"]');

    phoneFields.forEach(field => {
        field.addEventListener("input", function () {
            let digits = field.value.replace(/\D/g, "").slice(0, 10);

            if (digits.length > 6) {
                field.value = digits.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3");
            } else if (digits.length > 3) {
                field.value = digits.replace(/(\d{3})(\d+)/, "$1-$2");
            } else {
                field.value = digits;
            }
        });
    });
});


// -----------------------------------------
// SALARY FORMATTING — on blur only
// -----------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    const salary = document.getElementById("salary");

    if (salary) {
        salary.addEventListener("blur", function () {
            let value = parseFloat(salary.value);
            if (!isNaN(value)) {
                salary.value = value.toFixed(2);
            }
        });
    }
});


// -----------------------------------------
// AUTO-CLOSE ALERTS (sweet fade-out)
// -----------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-6px)';
            setTimeout(() => alert.remove(), 600);
        }, 3500);
    });
});


// -----------------------------------------
// FORM VALIDATION (Bootstrap)
// -----------------------------------------
(function () {
    "use strict";
    const forms = document.querySelectorAll(".needs-validation");

    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add("was-validated");
        }, false);
    });
})();


// -----------------------------------------
// SIDEBAR TOGGLE (for mobile in future)
// -----------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.querySelector('.sidebar');
    const toggleBtn = document.querySelector('.sidebar-toggle');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle("open");
        });
    }
});
