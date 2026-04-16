const API_USUARIO_AUTENTICADO = "/usuarioAutenticado";
const API_ORDENES_USUARIO = "/orden/useractual";
const API_CARRITO_MIO = "/carrito/mio";
const URL_IMAGENES = "/uploads/";
const IMAGEN_DEFAULT = "/images/default.png";

let ordenesUsuario = [];

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

    cargarBadgeCarrito();
    cargarOrdenesUsuario();
});

function cargarBadgeCarrito() {
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

            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = lista.length;
            });
        })
        .catch((error) => {
            console.error("Error cargando badge carrito:", error);
            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = "0";
            });
        });
}

function cargarOrdenesUsuario() {
    authFetch(API_USUARIO_AUTENTICADO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener usuario autenticado: " + res.status);
            }
            return res.json();
        })
        .then((usuario) => {
            if (!usuario || !usuario.id_usuario) {
                throw new Error("No se encontró id_usuario");
            }

            return authFetch(`${API_ORDENES_USUARIO}?id=${encodeURIComponent(usuario.id_usuario)}`);
        })
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener órdenes: " + res.status);
            }
            return res.json();
        })
        .then((ordenes) => {
            ordenesUsuario = Array.isArray(ordenes) ? ordenes : [];
            renderizarOrdenes(ordenesUsuario);
        })
        .catch((error) => {
            console.error("Error cargando órdenes:", error);

            const contenedor = document.getElementById("contenedorOrdenes");
            if (contenedor) {
                contenedor.innerHTML = `
                    <div class="col-12 text-center">
                        <h5>Error al cargar las órdenes</h5>
                    </div>
                `;
            }
        });
}

function renderizarOrdenes(ordenes) {
    const contenedor = document.getElementById("contenedorOrdenes");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (!ordenes || ordenes.length === 0) {
        contenedor.innerHTML = `
            <div class="col-12 text-center">
                <h5>No has realizado órdenes todavía</h5>
            </div>
        `;
        return;
    }

    ordenes.forEach((orden, index) => {
        const cantidadProductos = Array.isArray(orden.listaOrdenDetalle)
            ? orden.listaOrdenDetalle.length
            : 0;

        const card = `
            <div class="col-lg-4 col-md-6 col-sm-12 pb-4">
                <div class="card border-secondary orden-card" onclick="abrirModalOrden(${index})">
                    <div class="card-header bg-secondary border-0 d-flex justify-content-between align-items-center">
                        <h6 class="m-0 font-weight-semi-bold">Orden #${orden.id_orden}</h6>
                        <span class="estado-badge ${obtenerClaseEstado(orden.estado)}">
                            ${orden.estado || "SIN ESTADO"}
                        </span>
                    </div>

                    <div class="card-body">
                        <p class="mb-2"><strong>Fecha:</strong> ${formatearFecha(orden.fecha_creacion)}</p>
                        <p class="mb-2"><strong>Total:</strong> ₡${Number(orden.total || 0).toFixed(2)}</p>
                        <p class="mb-2"><strong>Productos:</strong> ${cantidadProductos}</p>
                        <p class="mb-0"><strong>Dirección:</strong> ${orden.direccion?.direccion || "No disponible"}</p>
                    </div>

                    <div class="card-footer bg-transparent border-secondary text-center">
                        <span class="text-primary font-weight-semi-bold">
                            Ver detalle
                        </span>
                    </div>
                </div>
            </div>
        `;

        contenedor.insertAdjacentHTML("beforeend", card);
    });
}

function obtenerClaseEstado(estado) {
    const valor = String(estado || "").trim().toUpperCase();

    if (valor === "CANCELADO" || valor === "CANCELADA") {
        return "estado-rojo";
    }

    if (valor === "PENDIENTE" || valor === "EN PROCESO") {
        return "estado-amarillo";
    }

    if (valor === "ENTREGADO" || valor === "ENTREGADA") {
        return "estado-verde";
    }

    return "estado-amarillo";
}

function formatearFecha(fecha) {
    if (!fecha) return "No disponible";

    const date = new Date(fecha);

    if (isNaN(date.getTime())) return fecha;

    return date.toLocaleString("es-CR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function abrirModalOrden(index) {
    const orden = ordenesUsuario[index];
    if (!orden) return;

    const estadoEl = document.getElementById("modalEstadoOrden");

    document.getElementById("modalIdOrden").textContent = orden.id_orden || "";
    document.getElementById("modalTotalOrden").textContent = `₡${Number(orden.total || 0).toFixed(2)}`;
    document.getElementById("modalFechaOrden").textContent = formatearFecha(orden.fecha_creacion);
    document.getElementById("modalNombreUsuario").textContent = orden.usuario?.nombre || "";
    document.getElementById("modalEmailUsuario").textContent = orden.usuario?.email || "";

    document.getElementById("modalDireccion").textContent = orden.direccion?.direccion || "";
    document.getElementById("modalCiudad").textContent = orden.direccion?.ciudad || "";
    document.getElementById("modalProvincia").textContent = orden.direccion?.provincia || "";
    document.getElementById("modalCodigoPostal").textContent = orden.direccion?.codigo_postal || "";
    document.getElementById("modalTelefono").textContent = orden.direccion?.telefono || "";

    if (estadoEl) {
        estadoEl.innerHTML = `
            <span class="estado-badge ${obtenerClaseEstado(orden.estado)}">
                ${orden.estado || "SIN ESTADO"}
            </span>
        `;
    }

    renderizarProductosModal(orden.listaOrdenDetalle || []);

    $("#modalOrdenDetalle").modal("show");
}

function renderizarProductosModal(listaOrdenDetalle) {
    const contenedor = document.getElementById("modalListaProductos");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (!listaOrdenDetalle || listaOrdenDetalle.length === 0) {
        contenedor.innerHTML = `
            <p class="mb-0">No hay productos asociados a esta orden.</p>
        `;
        return;
    }

    const cacheBust = Date.now();

    listaOrdenDetalle.forEach((detalle) => {
        const producto = detalle.producto || {};
        const nombreImagen = producto.imagen
            ? encodeURIComponent(String(producto.imagen).trim())
            : "";

        const srcImagen = nombreImagen
            ? `${URL_IMAGENES}${nombreImagen}?t=${cacheBust}`
            : IMAGEN_DEFAULT;

        const precioLinea = Number(detalle.precio || 0);
        const cantidad = Number(detalle.cantidad || 0);
        const precioUnitario = cantidad > 0 ? precioLinea / cantidad : 0;

        contenedor.insertAdjacentHTML(
            "beforeend",
            `
            <div class="row border-bottom py-3 align-items-center">
                <div class="col-md-2 col-3">
                    <img
                        src="${srcImagen}"
                        alt="${producto.nombre || "Producto"}"
                        class="img-fluid producto-modal-img"
                        onerror="this.onerror=null; this.src='${IMAGEN_DEFAULT}';"
                    />
                </div>

                <div class="col-md-6 col-9">
                    <h6 class="mb-1">${producto.nombre || "Producto sin nombre"}</h6>
                    <small class="text-muted d-block">${producto.descripcion || ""}</small>
                    <small class="text-muted d-block">
                        Categoría: ${producto.categoria?.nombre || "Sin categoría"}
                    </small>
                </div>

                <div class="col-md-4 col-12 text-md-right mt-3 mt-md-0">
                    <div><strong>Cantidad:</strong> ${cantidad}</div>
                    <div><strong>Unitario:</strong> ₡${precioUnitario.toFixed(2)}</div>
                    <div><strong>Total:</strong> ₡${precioLinea.toFixed(2)}</div>
                </div>
            </div>
            `
        );
    });
}