const container = document.getElementById('container');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');
const forgotPasswordLink = document.getElementById('forgotPasswordLink');

registerBtn?.addEventListener('click', () => {
    container.classList.add("active");
});

loginBtn?.addEventListener('click', () => {
    container.classList.remove("active");
});

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

/* =========================
   LOGIN
   ========================= */

document.getElementById("loginForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value.trim();

    if (!email || !password) {
        Swal.fire({
            icon: "warning",
            title: "Campos incompletos",
            text: "Debe ingresar su email y contraseña.",
            heightAuto: false
        });
        return;
    }

    fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
        .then(async res => {
            if (!res.ok) {
                let mensaje = "Credenciales incorrectas";

                try {
                    const errorData = await res.json();
                    mensaje = errorData.message || mensaje;
                } catch (_) {}

                throw new Error(mensaje);
            }

            return res.json();
        })
        .then(data => {
            localStorage.setItem("token", data.token);

            const decoded = parseJwt(data.token);

            if (!decoded || !decoded.role) {
                throw new Error("No se pudo verificar el rol del usuario");
            }

            const role = decoded.role;

            if (role === "ROLE_ADMIN" || role === "ROLE_EMPLEADO") {
                window.location.href = "/administrador/index.html";
            } else {
                window.location.href = "/ecommerce/shop.html";
            }
        })
        .catch(err => {
            Swal.fire({
                icon: "error",
                title: "Error al iniciar sesión",
                text: err.message,
                heightAuto: false
            });
        });
});

/* =========================
   REGISTER
   ========================= */

document.getElementById("registerForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    const nombre = document.getElementById("regNombre").value.trim();
    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value.trim();

    if (!nombre || !email || !password) {
        Swal.fire({
            icon: "warning",
            title: "Campos incompletos",
            text: "Debe completar nombre, email y contraseña.",
            heightAuto: false
        });
        return;
    }

    if (password.length < 8) {
        Swal.fire({
            icon: "warning",
            title: "Contraseña inválida",
            text: "La contraseña debe tener al menos 8 caracteres.",
            heightAuto: false
        });
        return;
    }

    fetch("/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nombre: nombre,
            email: email,
            password: password
        })
    })
        .then(async res => {
            if (!res.ok) {
                let mensaje = "Error al registrar";

                try {
                    const errorData = await res.json();
                    mensaje = errorData.message || mensaje;
                } catch (_) {}

                throw new Error(mensaje);
            }

            return res.json();
        })
        .then(data => {
            localStorage.setItem("token", data.token);

            Swal.fire({
                icon: "success",
                title: "Registro exitoso",
                text: "Su cuenta fue creada correctamente.",
                timer: 1500,
                showConfirmButton: false,
                heightAuto: false
            }).then(() => {
                window.location.href = "/ecommerce/shop.html";
            });
        })
        .catch(err => {
            Swal.fire({
                icon: "error",
                title: "Error al registrarse",
                text: err.message,
                heightAuto: false
            });
        });
});

/* =========================
   FORGOT PASSWORD
   ========================= */

forgotPasswordLink?.addEventListener("click", async function (e) {
    e.preventDefault();

    const { value: email } = await Swal.fire({
        title: "Recuperar contraseña",
        input: "email",
        inputLabel: "Ingrese su correo electrónico",
        inputPlaceholder: "correo@ejemplo.com",
        confirmButtonText: "Enviar",
        showCancelButton: true,
        cancelButtonText: "Cancelar",
        heightAuto: false,
        inputValidator: (value) => {
            if (!value) {
                return "Debe ingresar un correo";
            }
        }
    });

    if (!email) return;

    try {
        const res = await fetch("/auth/forgot-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email })
        });

        const data = await res.json().catch(() => ({}));

        if (!res.ok) {
            throw new Error(data.message || "No se pudo procesar la solicitud");
        }

        Swal.fire({
            icon: "success",
            title: "Solicitud enviada",
            text: data.message || "Revise su correo para continuar con el proceso.",
            heightAuto: false
        });

    } catch (error) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message,
            heightAuto: false
        });
    }
});