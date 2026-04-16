const API_CARRITO_MIO = "/carrito/mio";
const API_BORRAR_CARRITO_DETALLE = "/auth/carritodetalle/borrar";
const API_EDITAR_CARRITO_DETALLE = "/carritodetalle/editar";
const URL_IMAGENES = "/uploads/";
const IMAGEN_DEFAULT = "/images/default.png";
const COSTO_ENVIO = 0;

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

    cargarCarrito();
});

function cargarCarrito() {
    authFetch(API_CARRITO_MIO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener carrito: " + res.status);
            }
            return res.json();
        })
        .then((carrito) => {
            actualizarBadgeCarrito(carrito);
            renderizarCarrito(carrito);
            renderizarResumen(carrito);
            actualizarBotonProcederPago(carrito);
        })
        .catch((error) => {
            console.error("Error cargando carrito:", error);

            const tabla = document.getElementById("tablaCarrito");
            if (tabla) {
                tabla.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center">Error al cargar el carrito</td>
                    </tr>
                `;
            }

            const btnProcederPago = document.getElementById("btnProcederPago");
            if (btnProcederPago) {
                btnProcederPago.classList.add("disabled");
                btnProcederPago.setAttribute("aria-disabled", "true");
                btnProcederPago.style.pointerEvents = "none";
                btnProcederPago.style.opacity = "0.65";
            }

            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = "0";
            });
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

function renderizarCarrito(carrito) {
    const tabla = document.getElementById("tablaCarrito");
    if (!tabla) return;

    tabla.innerHTML = "";

    const lista = Array.isArray(carrito.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    if (lista.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5" class="text-center">No hay productos en el carrito</td>
            </tr>
        `;
        return;
    }

    const cacheBust = Date.now();

    lista.forEach((detalle) => {
        const producto = detalle.producto || {};
        const nombreImagen = producto.imagen
            ? encodeURIComponent(producto.imagen.trim())
            : "";

        const srcImagen = nombreImagen
            ? `${URL_IMAGENES}${nombreImagen}?t=${cacheBust}`
            : IMAGEN_DEFAULT;

        const precioUnitario = detalle.cantidad > 0
            ? Number(detalle.precio || 0) / Number(detalle.cantidad)
            : 0;

        const totalLinea = Number(detalle.precio || 0);

        const fila = `
            <tr>
                <td class="align-middle">
                    <img src="${srcImagen}" alt="${producto.nombre || "Producto"}" style="width: 50px"
                         onerror="this.onerror=null; this.src='${IMAGEN_DEFAULT}';" />
                    ${producto.nombre || "Producto sin nombre"}
                </td>

                <td class="align-middle">₡${precioUnitario.toFixed(2)}</td>

                <td class="align-middle">
                    <div class="input-group quantity mx-auto" style="width: 100px">
                        <div class="input-group-btn">
                            <button class="btn btn-sm btn-primary btn-minus"
                                onclick="cambiarCantidadDetalle(${detalle.id_carrito_detalle}, -1)">
                                <i class="fa fa-minus"></i>
                            </button>
                        </div>

                        <input
                            type="text"
                            class="form-control form-control-sm bg-secondary text-center"
                            value="${detalle.cantidad}"
                            readonly
                        />

                        <div class="input-group-btn">
                            <button class="btn btn-sm btn-primary btn-plus"
                                onclick="cambiarCantidadDetalle(${detalle.id_carrito_detalle}, 1)">
                                <i class="fa fa-plus"></i>
                            </button>
                        </div>
                    </div>
                </td>

                <td class="align-middle">₡${totalLinea.toFixed(2)}</td>

                <td class="align-middle">
                    <button class="btn btn-sm btn-primary"
                        onclick="eliminarDetalleCarrito(${detalle.id_carrito_detalle})">
                        <i class="fa fa-times"></i>
                    </button>
                </td>
            </tr>
        `;

        tabla.insertAdjacentHTML("beforeend", fila);
    });
}

function renderizarResumen(carrito) {
    const lista = Array.isArray(carrito.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    const subtotal = lista.reduce((acc, detalle) => {
        return acc + Number(detalle.precio || 0);
    }, 0);

    const envio = lista.length > 0 ? COSTO_ENVIO : 0;
    const total = subtotal + envio;

    const subtotalEl = document.getElementById("subtotalCarrito");
    const envioEl = document.getElementById("envioCarrito");
    const totalEl = document.getElementById("totalCarrito");

    if (subtotalEl) subtotalEl.textContent = `₡${subtotal.toFixed(2)}`;
    if (envioEl) envioEl.textContent = `₡${envio.toFixed(2)}`;
    if (totalEl) totalEl.textContent = `₡${total.toFixed(2)}`;
}

function eliminarDetalleCarrito(idCarritoDetalle) {
    authFetch(`${API_BORRAR_CARRITO_DETALLE}?id=${encodeURIComponent(idCarritoDetalle)}`, {
        method: "DELETE"
    })
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al borrar detalle: " + res.status);
            }
            return res.text();
        })
        .then((mensaje) => {
            Swal.fire({
                icon: "success",
                title: "Producto eliminado",
                text: mensaje,
                confirmButtonText: "Aceptar"
            });

            cargarCarrito();
        })
        .catch((error) => {
            console.error("Error borrando detalle:", error);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo eliminar el producto del carrito",
                confirmButtonText: "Aceptar"
            });
        });
}

function actualizarBotonProcederPago(carrito) {
    const btnProcederPago = document.getElementById("btnProcederPago");
    if (!btnProcederPago) return;

    const lista = Array.isArray(carrito.listaCarritoDetalles)
        ? carrito.listaCarritoDetalles
        : [];

    const hayProductos = lista.length > 0;

    if (hayProductos) {
        btnProcederPago.classList.remove("disabled");
        btnProcederPago.setAttribute("aria-disabled", "false");
        btnProcederPago.style.pointerEvents = "auto";
        btnProcederPago.style.opacity = "1";
    } else {
        btnProcederPago.classList.add("disabled");
        btnProcederPago.setAttribute("aria-disabled", "true");
        btnProcederPago.style.pointerEvents = "none";
        btnProcederPago.style.opacity = "0.65";
    }
}

function cambiarCantidadDetalle(idCarritoDetalle, cambio) {
    authFetch(API_CARRITO_MIO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener carrito: " + res.status);
            }
            return res.json();
        })
        .then((carrito) => {
            const lista = Array.isArray(carrito.listaCarritoDetalles)
                ? carrito.listaCarritoDetalles
                : [];

            const detalle = lista.find((item) => item.id_carrito_detalle === idCarritoDetalle);

            if (!detalle) {
                throw new Error("Detalle de carrito no encontrado");
            }

            const cantidadActual = Number(detalle.cantidad || 0);
            const nuevaCantidad = cantidadActual + cambio;

            if (nuevaCantidad <= 0) {
                return eliminarDetalleCarrito(idCarritoDetalle);
            }

            const precioUnitario = cantidadActual > 0
                ? Number(detalle.precio || 0) / cantidadActual
                : Number(detalle.producto?.precio || 0);

            const nuevoPrecio = precioUnitario * nuevaCantidad;

            const payload = {
                cantidad: nuevaCantidad,
                precio: nuevoPrecio,
                id_carrito: carrito.id_carrito,
                id_producto: detalle.producto.id_producto,
                id_carrito_detalle: detalle.id_carrito_detalle
            };

            return authFetch(API_EDITAR_CARRITO_DETALLE, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });
        })
        .then((res) => {
            if (!res) return;
            if (!res.ok) {
                throw new Error("Error al editar detalle: " + res.status);
            }
            return res.text();
        })
        .then(() => {
            cargarCarrito();
        })
        .catch((error) => {
            console.error("Error cambiando cantidad:", error);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo actualizar la cantidad",
                confirmButtonText: "Aceptar"
            });
        });
}