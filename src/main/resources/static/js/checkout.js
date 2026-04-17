const API_CARRITO_MIO = "/carrito/mio";
const API_DIRECCION_MIA = "/direccion/mia";
const API_EDITAR_DIRECCION = "/direccion/editar";
const API_CREAR_ORDEN = "/orden/crear";
const API_USUARIO_AUTENTICADO = "/usuarioAutenticado";
const API_PRODUCTO_BUSCAR = "/producto/buscar";

const API_PAYPAL_CREATE_ORDER = "/paypal/create-order";
const API_PAYPAL_CAPTURE_ORDER = "/paypal/capture-order";

let carritoActual = null;
let direccionActual = null;
let usuarioActual = null;
let paypalBotonesRenderizados = false;

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

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) return;

    configurarValidacionDireccion();
    configurarBotonFallback();

    Promise.all([
        cargarCarrito(),
        cargarMiDireccion(),
        cargarUsuarioAutenticado()
    ])
        .then(() => {
            validarDireccionCompleta();
            inicializarPaypal();
        })
        .catch((error) => {
            console.error("Error inicializando checkout:", error);
            validarDireccionCompleta();
        });
});

function configurarBotonFallback() {
    const btnHacerPago = document.getElementById("btnHacerPago");
    if (!btnHacerPago) return;

    btnHacerPago.addEventListener("click", async () => {
        Swal.fire({
            icon: "info",
            title: "PayPal no disponible",
            text: "No se cargó el botón de PayPal. Verifica que el SDK de PayPal esté incluido correctamente.",
            confirmButtonText: "Aceptar",
            heightAuto: false
        });
    });
}

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
    const lista = Array.isArray(carrito?.listaCarritoDetalles)
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

    const lista = Array.isArray(carrito?.listaCarritoDetalles)
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
                <p>${escapeHtml(nombre)}</p>
                <p>${formatearMontoCRC(precio)}</p>
            </div>
            `
        );
    });

    if (subtotalOrden) subtotalOrden.textContent = formatearMontoCRC(subtotal);
    if (totalOrden) totalOrden.textContent = formatearMontoCRC(subtotal);
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

    if (btnHacerPago) {
        btnHacerPago.disabled = !puedePagar;
    }

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

async function validarStockAntesDePagar() {
    if (!carritoActual || !Array.isArray(carritoActual.listaCarritoDetalles)) {
        throw new Error("No se pudo validar el carrito actual");
    }

    const lista = carritoActual.listaCarritoDetalles;

    if (lista.length === 0) {
        throw new Error("No hay productos en el carrito");
    }

    const resultados = await Promise.all(
        lista.map(async (detalle) => {
            const idProducto =
                detalle?.producto?.id_producto ??
                detalle?.id_producto ??
                null;

            const nombreProducto =
                detalle?.producto?.nombre ??
                "Producto sin nombre";

            const cantidadSolicitada = Number(detalle?.cantidad ?? 0);

            if (!idProducto) {
                throw new Error(`No se encontró el id del producto "${nombreProducto}"`);
            }

            const res = await authFetch(`${API_PRODUCTO_BUSCAR}?id=${encodeURIComponent(idProducto)}`);

            if (!res.ok) {
                throw new Error(`No se pudo consultar el stock del producto "${nombreProducto}"`);
            }

            const producto = await res.json();
            const stockActual = Number(producto?.stock_actual ?? 0);

            return {
                idProducto,
                nombreProducto: producto?.nombre || nombreProducto,
                cantidadSolicitada,
                stockActual
            };
        })
    );

    const sinStock = resultados.filter((item) => item.cantidadSolicitada > item.stockActual);

    if (sinStock.length > 0) {
        const mensaje = sinStock
            .map((item) =>
                `${item.nombreProducto}: solicitados ${item.cantidadSolicitada}, disponibles ${item.stockActual}`
            )
            .join("\n");

        Swal.fire({
            icon: "warning",
            title: "Stock insuficiente",
            text: mensaje,
            confirmButtonText: "Aceptar",
            heightAuto: false
        });

        return false;
    }

    return true;
}

function convertirCRCaUSD(montoCRC) {
    const tipoCambio = 520;
    const monto = Number(montoCRC || 0);

    return (monto / tipoCambio).toFixed(2);
}

function inicializarPaypal() {
    if (paypalBotonesRenderizados) return;

    const contenedorPaypal = document.getElementById("paypal-button-container");
    if (!contenedorPaypal) {
        console.warn("No se encontró #paypal-button-container");
        return;
    }

    if (typeof paypal === "undefined") {
        console.warn("PayPal SDK no está cargado");
        return;
    }

    paypal.Buttons({
        style: {
            layout: "vertical",
            color: "gold",
            shape: "rect",
            label: "paypal"
        },

        onClick: async function () {
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
                    confirmButtonText: "Aceptar",
                    heightAuto: false
                });

                return false;
            }

            try {
                const stockValido = await validarStockAntesDePagar();
                return stockValido;
            } catch (error) {
                console.error("Error validando stock:", error);

                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message || "No se pudo validar el stock de los productos",
                    confirmButtonText: "Aceptar",
                    heightAuto: false
                });

                return false;
            }
        },

        createOrder: async function () {
            await guardarDireccionActualizada();

            const totalCRC = calcularTotalCarrito(carritoActual);

            if (!totalCRC || totalCRC <= 0) {
                throw new Error("El total del carrito es inválido");
            }

            const totalUSD = convertirCRCaUSD(totalCRC);

            const res = await authFetch(API_PAYPAL_CREATE_ORDER, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    total: totalUSD
                })
            });

            const data = await res.json().catch(() => ({}));

            if (!res.ok) {
                throw new Error(data.message || "No se pudo crear la orden en PayPal");
            }

            if (!data.orderId) {
                throw new Error("El backend no devolvió orderId de PayPal");
            }

            return data.orderId;
        },

        onApprove: async function (data) {
            try {
                const captureRes = await authFetch(API_PAYPAL_CAPTURE_ORDER, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        orderId: data.orderID
                    })
                });

                const captureData = await captureRes.json().catch(() => ({}));

                if (!captureRes.ok) {
                    throw new Error(captureData.message || "No se pudo capturar el pago en PayPal");
                }

                await crearOrdenInternaDespuesDePago();

                Swal.fire({
                    icon: "success",
                    title: "Pago realizado",
                    text: "El pago fue aprobado y la orden fue creada correctamente",
                    confirmButtonText: "Aceptar",
                    heightAuto: false
                }).then(() => {
                    window.location.href = "orders.html";
                });

            } catch (error) {
                console.error("Error capturando pago:", error);

                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message || "No se pudo completar el pago",
                    confirmButtonText: "Aceptar",
                    heightAuto: false
                });
            }
        },

        onError: function (err) {
            console.error("Error PayPal:", err);

            Swal.fire({
                icon: "error",
                title: "Error en PayPal",
                text: "No se pudo completar el pago con PayPal",
                confirmButtonText: "Aceptar",
                heightAuto: false
            });
        }
    }).render("#paypal-button-container");

    paypalBotonesRenderizados = true;
}

function crearOrdenInternaDespuesDePago() {
    return Promise.all([
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
    ])
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
                throw new Error("No hay productos para procesar");
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
            if (!res.ok) {
                throw new Error("Error al crear orden: " + res.status);
            }

            return res.text();
        });
}

function formatearMontoCRC(valor) {
    const numero = Number(valor || 0);

    return new Intl.NumberFormat("es-CR", {
        style: "currency",
        currency: "CRC",
        minimumFractionDigits: 2
    }).format(numero);
}

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}