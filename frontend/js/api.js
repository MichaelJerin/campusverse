const API_BASE_URL = "http://localhost:8080/api";

function saveAuth(token, name, email, role) {
    localStorage.setItem("cv_token", token);
    localStorage.setItem("cv_name", name);
    localStorage.setItem("cv_email", email);
    localStorage.setItem("cv_role", role);
}

function getToken() { return localStorage.getItem("cv_token"); }
function getUserName() { return localStorage.getItem("cv_name"); }

function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

function requireAuth() {
    if (!getToken()) window.location.href = "index.html";
}

// Endpoints that must never carry a token - even a stale one - or
// JwtAuthFilter will try to validate it and can 403 a plain login/register.
const PUBLIC_PATHS = ["/auth/login", "/auth/register"];

async function apiFetch(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    const isPublic = PUBLIC_PATHS.some(p => path.startsWith(p));
    const token = getToken();
    if (token && !isPublic) headers["Authorization"] = "Bearer " + token;

    let response;
    try {
        response = await fetch(API_BASE_URL + path, { ...options, headers });
    } catch (networkErr) {
        throw new Error("Can't reach the server. Make sure the backend is running, then try again.");
    }

    let data = null;
    try { data = await response.json(); } catch (e) { /* no JSON body */ }

    if (!response.ok) {
        const err = new Error(data?.message || `Something went wrong (${response.status}). Please try again.`);
        err.fieldErrors = data?.fieldErrors || null;
        err.status = response.status;
        throw err;
    }
    return data;
}

// ---- Form error display helpers ----
function showError(message) {
    const box = document.getElementById("errorBox");
    if (!box) return;
    box.textContent = message;
    box.style.display = "block";
}

function hideError() {
    const box = document.getElementById("errorBox");
    if (box) box.style.display = "none";
}

function clearFieldErrors(formEl) {
    formEl.querySelectorAll(".field-error").forEach(el => el.remove());
    formEl.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"));
}

function showFieldErrors(formEl, fieldErrors) {
    Object.entries(fieldErrors).forEach(([field, message]) => {
        const input = formEl.querySelector("#" + field);
        if (input) {
            input.classList.add("is-invalid");
            const msg = document.createElement("div");
            msg.className = "field-error";
            msg.textContent = message;
            input.insertAdjacentElement("afterend", msg);
        }
    });
}

function handleFormError(formEl, err) {
    clearFieldErrors(formEl);
    if (err.fieldErrors) {
        showFieldErrors(formEl, err.fieldErrors);
        showError("Please fix the highlighted fields.");
    } else {
        showError(err.message);
    }
}

// const API_BASE_URL = "http://localhost:8080/api";

// function saveAuth(token, name, email, role) {
//     localStorage.setItem("cv_token", token);
//     localStorage.setItem("cv_name", name);
//     localStorage.setItem("cv_email", email);
//     localStorage.setItem("cv_role", role);
// }

// function getToken() { return localStorage.getItem("cv_token"); }
// function getUserName() { return localStorage.getItem("cv_name"); }

// function logout() {
//     localStorage.clear();
//     window.location.href = "index.html";
// }

// function requireAuth() {
//     if (!getToken()) window.location.href = "index.html";
// }

// async function apiFetch(path, options = {}) {
//     const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
//     const token = getToken();
//     if (token) headers["Authorization"] = "Bearer " + token;

//     let response;
//     try {
//         response = await fetch(API_BASE_URL + path, { ...options, headers });
//     } catch (networkErr) {
//         throw new Error("Can't reach the server. Make sure the backend is running, then try again.");
//     }

//     let data = null;
//     try { data = await response.json(); } catch (e) { /* no JSON body */ }

//     if (!response.ok) {
//         const err = new Error(data?.message || `Something went wrong (${response.status}). Please try again.`);
//         err.fieldErrors = data?.fieldErrors || null;
//         err.status = response.status;
//         throw err;
//     }
//     return data;
// }

// // ---- Form error display helpers ----
// function showError(message) {
//     const box = document.getElementById("errorBox");
//     if (!box) return;
//     box.textContent = message;
//     box.style.display = "block";
// }

// function hideError() {
//     const box = document.getElementById("errorBox");
//     if (box) box.style.display = "none";
// }

// function clearFieldErrors(formEl) {
//     formEl.querySelectorAll(".field-error").forEach(el => el.remove());
//     formEl.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"));
// }

// function showFieldErrors(formEl, fieldErrors) {
//     Object.entries(fieldErrors).forEach(([field, message]) => {
//         const input = formEl.querySelector("#" + field);
//         if (input) {
//             input.classList.add("is-invalid");
//             const msg = document.createElement("div");
//             msg.className = "field-error";
//             msg.textContent = message;
//             input.insertAdjacentElement("afterend", msg);
//         }
//     });
// }

// function handleFormError(formEl, err) {
//     clearFieldErrors(formEl);
//     if (err.fieldErrors) {
//         showFieldErrors(formEl, err.fieldErrors);
//         showError("Please fix the highlighted fields.");
//     } else {
//         showError(err.message);
//     }
// }