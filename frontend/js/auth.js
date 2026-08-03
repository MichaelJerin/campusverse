const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideError();
        clearFieldErrors(loginForm);

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            const data = await apiFetch("/auth/login", {
                method: "POST",
                body: JSON.stringify({ email, password })
            });
            saveAuth(data.token, data.name, data.email, data.role);
            window.location.href = "dashboard.html";
        } catch (err) {
            handleFormError(loginForm, err);
        }
    });
}

const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideError();
        clearFieldErrors(registerForm);

        const payload = {
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            branch: document.getElementById("branch").value,
            degreeLevel: document.getElementById("degreeLevel").value,
            batchStartYear: parseInt(document.getElementById("batchStartYear").value) || null,
            batchEndYear: parseInt(document.getElementById("batchEndYear").value) || null
        };

        try {
            const data = await apiFetch("/auth/register", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            saveAuth(data.token, data.name, data.email, data.role);
            window.location.href = "dashboard.html";
        } catch (err) {
            handleFormError(registerForm, err);
        }
    });
}