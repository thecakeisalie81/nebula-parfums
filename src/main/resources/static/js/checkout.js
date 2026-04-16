const API_CARRITO_MIO = "/carrito/mio";
const API_DIRECCION_MIA = "/direccion/mia";
const API_EDITAR_DIRECCION = "/direccion/editar";
const API_CREAR_ORDEN = "/orden/crear";
const API_USUARIO_AUTENTICADO = "/usuarioAutenticado";

let carritoActual = null;
let direccionActual = null;
let usuarioActual = null;

function obtenerToken() {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
        console.error("Token inválido");
        window.location.href = "/login.html";
        return null;
    }

    return token;
}

function authFetch(url, options = {}) {
    const token = obtenerToken();

    if (!token) {
        return Promise.reject("Token no encontrado");
    }

    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            Authorization: "Bearer " + token
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) return;

    configurarValidacionDireccion();

    Promise.all([
        cargarCarrito(),
        cargarMiDireccion(),
        cargarUsuarioAutenticado()
    ])
        .then(() => {
            validarDireccionCompleta();
        })
        .catch((error) => {
            console.error("Error inicializando checkout:", error);
            validarDireccionCompleta();
        });

    const btnHacerPago = document.getElementById("btnHacerPago");
    if (btnHacerPago) {
        btnHacerPago.addEventListener("click", crearOrden);
    }
});

function cargarCarrito() {
    return authFetch(API_CARRITO_MIO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener carrito: " + res.status);
            }
            return res.json();
        })
        .then((carrito) => {
            carritoActual = carrito;
            actualizarBadgeCarrito(carrito);
            renderizarResumenOrden(carrito);
            return carrito;
        })
        .catch((error) => {
            console.error("Error cargando carrito:", error);

            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = "0";
            });

            const listaProductosOrden = document.getElementById("listaProductosOrden");
            if (listaProductosOrden) {
                listaProductosOrden.innerHTML = `
                    <p class="text-center mb-0">Error al cargar el checkout</p>
                `;
            }

            const subtotalOrden = document.getElementById("subtotalOrden");
            const totalOrden = document.getElementById("totalOrden");

            if (subtotalOrden) subtotalOrden.textContent = "₡0.00";
            if (totalOrden) totalOrden.textContent = "₡0.00";

            throw error;
        });
}

function cargarMiDireccion() {
    return authFetch(API_DIRECCION_MIA)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener dirección: " + res.status);
            }
            return res.json();
        })
        .then((direccion) => {
            direccionActual = direccion;
            renderizarDireccion(direccion);
            return direccion;
        })
        .catch((error) => {
            console.error("Error cargando dirección:", error);
            direccionActual = null;
            validarDireccionCompleta();
            throw error;
        });
}

function cargarUsuarioAutenticado() {
    return authFetch(API_USUARIO_AUTENTICADO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener usuario autenticado: " + res.status);
            }
            return res.json();
        })
        .then((usuario) => {
            usuarioActual = usuario;
            return usuario;
        })
        .catch((error) => {
            console.error("Error cargando usuario autenticado:", error);
            usuarioActual = null;
            throw error;
        });
}

function actualizarBadgeCarrito(carrito) {
    const lista = Array.isArray(carrito.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    const totalDetalles = lista.length;

    document.querySelectorAll(".badge-carrito").forEach((badge) => {
        badge.textContent = totalDetalles;
    });
}

function renderizarResumenOrden(carrito) {
    const listaProductosOrden = document.getElementById("listaProductosOrden");
    const subtotalOrden = document.getElementById("subtotalOrden");
    const totalOrden = document.getElementById("totalOrden");

    if (!listaProductosOrden) return;

    listaProductosOrden.innerHTML = "";

    const lista = Array.isArray(carrito.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    if (lista.length === 0) {
        listaProductosOrden.innerHTML = `
            <p class="text-center mb-0">No hay productos en el carrito</p>
        `;

        if (subtotalOrden) subtotalOrden.textContent = "₡0.00";
        if (totalOrden) totalOrden.textContent = "₡0.00";
        return;
    }

    const subtotal = calcularTotalCarrito(carrito);

    lista.forEach((detalle) => {
        const nombre = detalle.producto?.nombre || "Producto sin nombre";
        const precio = Number(detalle.precio || 0);

        listaProductosOrden.insertAdjacentHTML(
            "beforeend",
            `
            <div class="d-flex justify-content-between">
                <p>${nombre}</p>
                <p>₡${precio.toFixed(2)}</p>
            </div>
            `
        );
    });

    if (subtotalOrden) subtotalOrden.textContent = `₡${subtotal.toFixed(2)}`;
    if (totalOrden) totalOrden.textContent = `₡${subtotal.toFixed(2)}`;
}

function calcularTotalCarrito(carrito) {
    const lista = Array.isArray(carrito?.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    return lista.reduce((acc, detalle) => {
        return acc + Number(detalle.precio || 0);
    }, 0);
}

function obtenerCamposDireccion() {
    return {
        ciudadInput: document.getElementById("ciudadDireccion"),
        provinciaInput: document.getElementById("provinciaDireccion"),
        telefonoInput: document.getElementById("telefonoDireccion"),
        direccionInput: document.getElementById("direccionExacta"),
        codigoPostalInput: document.getElementById("codigoPostalDireccion")
    };
}

function configurarValidacionDireccion() {
    const {
        ciudadInput,
        provinciaInput,
        telefonoInput,
        direccionInput,
        codigoPostalInput
    } = obtenerCamposDireccion();

    [ciudadInput, provinciaInput, telefonoInput, direccionInput, codigoPostalInput].forEach((campo) => {
        if (!campo) return;

        campo.addEventListener("input", validarDireccionCompleta);
        campo.addEventListener("change", validarDireccionCompleta);
    });

    validarDireccionCompleta();
}

function normalizarTexto(valor) {
    return String(valor || "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

function asignarProvinciaSelect(select, provincia) {
    if (!select) return;

    const provinciaNormalizada = normalizarTexto(provincia).toLowerCase();
    let encontrada = false;

    Array.from(select.options).forEach((option) => {
        const valorNormalizado = normalizarTexto(option.value).toLowerCase();

        if (valorNormalizado === provinciaNormalizada) {
            select.value = option.value;
            encontrada = true;
        }
    });

    if (!encontrada) {
        select.value = "";
    }
}

function renderizarDireccion(direccion) {
    const {
        ciudadInput,
        provinciaInput,
        telefonoInput,
        direccionInput,
        codigoPostalInput
    } = obtenerCamposDireccion();

    if (ciudadInput) ciudadInput.value = direccion?.ciudad || "";
    if (provinciaInput) asignarProvinciaSelect(provinciaInput, direccion?.provincia || "");
    if (telefonoInput) telefonoInput.value = direccion?.telefono || "";
    if (direccionInput) direccionInput.value = direccion?.direccion || "";
    if (codigoPostalInput) codigoPostalInput.value = direccion?.codigo_postal || "";

    validarDireccionCompleta();
}

function validarDireccionCompleta() {
    const btnHacerPago = document.getElementById("btnHacerPago");
    const mensaje = document.getElementById("mensajeDireccionIncompleta");

    if (!btnHacerPago) return false;

    const {
        ciudadInput,
        provinciaInput,
        telefonoInput,
        direccionInput,
        codigoPostalInput
    } = obtenerCamposDireccion();

    const ciudad = ciudadInput ? String(ciudadInput.value || "").trim() : "";
    const provincia = provinciaInput ? String(provinciaInput.value || "").trim() : "";
    const telefono = telefonoInput ? String(telefonoInput.value || "").trim() : "";
    const direccion = direccionInput ? String(direccionInput.value || "").trim() : "";
    const codigoPostal = codigoPostalInput ? String(codigoPostalInput.value || "").trim() : "";

    const direccionCompleta =
        ciudad !== "" &&
        provincia !== "" &&
        telefono !== "" &&
        direccion !== "" &&
        codigoPostal !== "";

    const lista = Array.isArray(carritoActual?.listaCarritoDetalles)
        ? carritoActual.listaCarritoDetalles
        : [];

    const hayProductos = lista.length > 0;

    const puedePagar = direccionCompleta && hayProductos;

    btnHacerPago.disabled = !puedePagar;

    if (mensaje) {
        if (!hayProductos) {
            mensaje.textContent = "Debes tener al menos un producto en el carrito antes de hacer el pago.";
            mensaje.classList.remove("d-none");
        } else if (!direccionCompleta) {
            mensaje.textContent = "Debes completar todos los campos de dirección antes de hacer el pago.";
            mensaje.classList.remove("d-none");
        } else {
            mensaje.classList.add("d-none");
        }
    }

    return puedePagar;
}

function obtenerDireccionDesdeFormulario() {
    const {
        ciudadInput,
        provinciaInput,
        telefonoInput,
        direccionInput,
        codigoPostalInput
    } = obtenerCamposDireccion();

    return {
        direccion: direccionInput ? String(direccionInput.value || "").trim() : "",
        ciudad: ciudadInput ? String(ciudadInput.value || "").trim() : "",
        provincia: provinciaInput ? String(provinciaInput.value || "").trim() : "",
        codigo_postal: codigoPostalInput ? String(codigoPostalInput.value || "").trim() : "",
        telefono: telefonoInput ? String(telefonoInput.value || "").trim() : ""
    };
}

function guardarDireccionActualizada() {
    const payload = obtenerDireccionDesdeFormulario();

    return authFetch(API_EDITAR_DIRECCION, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al editar dirección: " + res.status);
            }
            return res.text();
        })
        .then(() => cargarMiDireccion());
}

function crearOrden() {
    if (!validarDireccionCompleta()) {
        const lista = Array.isArray(carritoActual?.listaCarritoDetalles)
            ? carritoActual.listaCarritoDetalles
            : [];

        const hayProductos = lista.length > 0;

        Swal.fire({
            icon: "warning",
            title: hayProductos ? "Dirección incompleta" : "Carrito vacío",
            text: hayProductos
                ? "Debes completar todos los campos de dirección antes de hacer el pago"
                : "Debes tener al menos un producto en el carrito antes de hacer el pago",
            confirmButtonText: "Aceptar"
        });
        return;
    }

    guardarDireccionActualizada()
        .then(() => Promise.all([
            authFetch(API_CARRITO_MIO).then((res) => {
                if (!res.ok) {
                    throw new Error("Error al obtener carrito: " + res.status);
                }
                return res.json();
            }),
            authFetch(API_USUARIO_AUTENTICADO).then((res) => {
                if (!res.ok) {
                    throw new Error("Error al obtener usuario autenticado: " + res.status);
                }
                return res.json();
            }),
            authFetch(API_DIRECCION_MIA).then((res) => {
                if (!res.ok) {
                    throw new Error("Error al obtener dirección: " + res.status);
                }
                return res.json();
            })
        ]))
        .then(([carrito, usuario, direccion]) => {
            carritoActual = carrito;
            usuarioActual = usuario;
            direccionActual = direccion;

            if (!carritoActual || !carritoActual.id_carrito) {
                throw new Error("No se encontró id_carrito en /carrito/mio");
            }

            if (!usuarioActual || !usuarioActual.id_usuario) {
                throw new Error("No se encontró id_usuario en /usuarioAutenticado");
            }

            if (!direccionActual || !direccionActual.id_direccion) {
                throw new Error("No se encontró id_direccion en /direccion/mia");
            }

            const lista = Array.isArray(carritoActual.listaCarritoDetalles)
                ? carritoActual.listaCarritoDetalles
                : [];

            if (lista.length === 0) {
                Swal.fire({
                    icon: "warning",
                    title: "Carrito vacío",
                    text: "No hay productos para procesar",
                    confirmButtonText: "Aceptar"
                });
                return null;
            }

            const total = calcularTotalCarrito(carritoActual);

            const payload = {
                id_direccion: direccionActual.id_direccion,
                id_usuario: usuarioActual.id_usuario,
                total: total,
                id_carrito: carritoActual.id_carrito
            };

            return authFetch(API_CREAR_ORDEN, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });
        })
        .then((res) => {
            if (!res) return null;

            if (!res.ok) {
                throw new Error("Error al crear orden: " + res.status);
            }

            return res.text();
        })
        .then((mensaje) => {
            if (!mensaje) return;

            Swal.fire({
                icon: "success",
                title: "Orden creada",
                text: mensaje,
                confirmButtonText: "Aceptar"
            }).then(() => {
                window.location.href = "shop.html";
            });
        })
        .catch((error) => {
            console.error("Error en checkout:", error);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "No se pudo completar el proceso",
                confirmButtonText: "Aceptar"
            });
        });
}