function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(atob(base64));
    } catch (error) {
        console.error("Token inválido:", error);
        return null;
    }
}

window.onload = function () {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.replace("/login.html");
        return;
    }

    const decoded = parseJwt(token);

    if (!decoded || !decoded.role) {
        localStorage.removeItem("token");
        window.location.replace("/login.html");
        return;
    }

    const role = decoded.role;

    if (role === "ROLE_CLIENTE") {
        window.location.replace("/ecommerce/shop.html");
        return;
    }

    if (role !== "ROLE_ADMIN" && role !== "ROLE_EMPLEADO") {
        localStorage.removeItem("token");
        window.location.replace("/login.html");
        return;
    }
};

const menuBtn = document.getElementById("menu-btn");
const closeBtn = document.getElementById("close-btn");
const aside = document.querySelector("aside");
const themeToggler = document.querySelector(".theme-toggler");
const logo = document.querySelector(".img-logo");
const logoutBtn = document.getElementById("logoutBtn");

menuBtn?.addEventListener("click", () => {
    aside?.classList.add("open");
});

closeBtn?.addEventListener("click", () => {
    aside?.classList.remove("open");
});

function setTheme(theme) {
    const isDark = theme === "dark";

    document.documentElement.classList.toggle("dark-theme-variables", isDark);

    if (logo) {
        logo.style.filter = isDark ? "invert(1)" : "none";
    }

    if (themeToggler) {
        themeToggler.querySelector("span:nth-child(1)")?.classList.toggle("active", !isDark);
        themeToggler.querySelector("span:nth-child(2)")?.classList.toggle("active", isDark);
    }

    localStorage.setItem("theme", theme);
}

function aplicarTemaGuardado() {
    const temaGuardado = localStorage.getItem("theme") || "light";
    setTheme(temaGuardado);
}

aplicarTemaGuardado();

themeToggler?.addEventListener("click", () => {
    const darkModeActivo = document.documentElement.classList.contains("dark-theme-variables");
    setTheme(darkModeActivo ? "light" : "dark");
});

/* =========================
   HELPERS AUTH
   ========================= */

function obtenerToken() {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
        return null;
    }

    return token;
}

function authFetch(url, options = {}) {
    const token = obtenerToken();

    if (!token) {
        return Promise.reject(new Error("Token no encontrado"));
    }

    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            Authorization: "Bearer " + token
        }
    });
}

/* =========================
   LOGOUT
   ========================= */

async function registrarLogout() {
    const res = await authFetch("/log/logout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({}),
        keepalive: true
    });

    if (!res.ok) {
        let mensaje = "No se pudo registrar el logout";
        try {
            const errorData = await res.json();
            mensaje = errorData.message || mensaje;
        } catch (_) {}
        throw new Error(mensaje);
    }
}

async function cerrarSesion() {
    try {
        await registrarLogout();
    } catch (error) {
        console.error("Error registrando logout:", error);
    } finally {
        localStorage.removeItem("token");
        window.location.href = "/login.html";
    }
}

logoutBtn?.addEventListener("click", async function (e) {
    e.preventDefault();
    await cerrarSesion();
});