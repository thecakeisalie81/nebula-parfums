const container = document.getElementById('container');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');

registerBtn.addEventListener('click', () => {
    container.classList.add("active");
});

loginBtn.addEventListener('click', () => {
    container.classList.remove("active");
});

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
}

document.getElementById("loginForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,       // ✅ AHORA CORRECTO
            password: password
        })
    })
        .then(res => {
            if (!res.ok) throw new Error("Credenciales incorrectas");
            return res.json();
        })
        .then(data => {
            localStorage.setItem("token", data.token);

            const decoded = parseJwt(data.token);
            const role = decoded.role;
            console.log(role)// 👈 depende de cómo lo generes

            // 🔥 REDIRECCIÓN
            if (role === "ROLE_ADMIN" || role === "ROLE_EMPLEADO") {
                window.location.href = "/administrador/index.html";
            } else {
                window.location.href = "/ecommerce.html";
            }
        })
});


document.getElementById("registerForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const nombre = document.getElementById("regNombre").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    fetch("/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nombre: nombre,
            email: email,     // ✅ consistente con backend
            password: password
        })
    })
        .then(res => {
            if (!res.ok) throw new Error("Error al registrar");
            return res.json();
        })
        .then(data => {
            localStorage.setItem("token", data.token);

            window.location.href = "/administrador/index.html";
        })
        .catch(err => alert(err.message));
});