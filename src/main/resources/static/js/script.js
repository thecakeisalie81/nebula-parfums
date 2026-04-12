window.onload = function() {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.replace("/login.html");
    }
};

document.getElementById("logoutBtn")?.addEventListener("click", function(e) {
    e.preventDefault();
    localStorage.removeItem("token");
    window.location.href = "/login.html";
});

const menuBtn = document.getElementById("menu-btn");
const closeBtn = document.getElementById("close-btn");
const aside = document.querySelector("aside");
const themeToggler = document.querySelector(".theme-toggler");
const logo = document.querySelector(".img-logo");

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
