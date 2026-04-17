function obtenerToken() {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
        console.error("[obtenerToken] Token inválido");
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

let paginaActual = 0;
let totalPaginas = 0;
let ordenesCache = [];
let ordenSeleccionadaActual = null;

let filtroEstado = "";
let filtroFechaInicio = "";
let filtroFechaFin = "";

document.addEventListener("DOMContentLoaded", () => {
    console.log("[DOMContentLoaded] pedidos.js cargado");

    const token = obtenerToken();
    if (!token) return;

    const estadoSelect = document.getElementById("estado");
    const fechaInicio = document.getElementById("fechaInicio");
    const fechaFin = document.getElementById("fechaFin");

    const modalOrden = document.getElementById("orderModal");
    const closeOrderModalBtn = document.getElementById("closeOrderModal");
    const formEditar = document.querySelector("#orderModal form");

    const modalProductos = document.getElementById("productosModal");
    const closeProductosModalBtn = document.getElementById("closeProductosModal");
    const btnVerProductos = document.getElementById("btnVerProductos");

    console.log("[DOMContentLoaded] estadoSelect:", estadoSelect);
    console.log("[DOMContentLoaded] fechaInicio:", fechaInicio);
    console.log("[DOMContentLoaded] fechaFin:", fechaFin);
    console.log("[DOMContentLoaded] modalOrden:", modalOrden);
    console.log("[DOMContentLoaded] modalProductos:", modalProductos);
    console.log("[DOMContentLoaded] btnVerProductos:", btnVerProductos);

    if (estadoSelect) {
        estadoSelect.addEventListener("change", function () {
            filtroEstado = this.value.trim();
            console.log("[Filtro estado] nuevo valor:", filtroEstado);
            cargarOrdenes(0);
        });
    }

    if (fechaInicio) {
        fechaInicio.addEventListener("change", function () {
            filtroFechaInicio = this.value;
            console.log("[Filtro fechaInicio] nuevo valor:", filtroFechaInicio);
            cargarOrdenes(0);
        });
    }

    if (fechaFin) {
        fechaFin.addEventListener("change", function () {
            filtroFechaFin = this.value;
            console.log("[Filtro fechaFin] nuevo valor:", filtroFechaFin);
            cargarOrdenes(0);
        });
    }

    if (closeOrderModalBtn && modalOrden) {
        closeOrderModalBtn.addEventListener("click", () => {
            modalOrden.style.display = "none";
        });
    }

    if (closeProductosModalBtn && modalProductos) {
        closeProductosModalBtn.addEventListener("click", () => {
            modalProductos.style.display = "none";
        });
    }

    if (btnVerProductos) {
        btnVerProductos.addEventListener("click", (e) => {
            e.preventDefault();
            abrirModalProductos();
        });
    }

    window.addEventListener("click", (event) => {
        if (event.target === modalOrden) {
            modalOrden.style.display = "none";
        }

        if (event.target === modalProductos) {
            modalProductos.style.display = "none";
        }
    });

    if (formEditar) {
        formEditar.addEventListener("submit", async (e) => {
            e.preventDefault();
            await actualizarEstadoOrden();
        });
    }

    cargarOrdenes(0);
    cargarIngresosTotales();
    cargarTotalesPedidos();
});

function construirUrlOrdenes(page = 0) {
    const params = new URLSearchParams();
    params.append("page", page);
    params.append("size", 10);

    if (filtroEstado) {
        params.append("estado", filtroEstado);
    }

    if (filtroFechaInicio) {
        params.append("fechaInicio", filtroFechaInicio);
    }

    if (filtroFechaFin) {
        params.append("fechaFin", filtroFechaFin);
    }

    const url = `/orden/filtrar?${params.toString()}`;
    console.log("[construirUrlOrdenes] URL:", url);
    return url;
}

function cargarOrdenes(page = 0) {
    const url = construirUrlOrdenes(page);
    console.log("[cargarOrdenes] iniciando carga, page:", page);

    authFetch(url)
        .then(async (res) => {
            console.log("[cargarOrdenes] status:", res.status);

            if (!res.ok) {
                let mensaje = "Error al cargar órdenes";
                try {
                    const errorData = await res.json();
                    console.error("[cargarOrdenes] errorData:", errorData);
                    mensaje = errorData.message || mensaje;
                } catch (_) {
                    console.warn("[cargarOrdenes] no se pudo parsear error JSON");
                }
                throw new Error(`${mensaje} (${res.status})`);
            }

            return res.json();
        })
        .then((data) => {
            console.log("[cargarOrdenes] respuesta:", data);

            const ordenes = data.content || [];
            const currentPage = data.number ?? data.page?.number ?? 0;
            const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

            paginaActual = currentPage;
            totalPaginas = totalPages;
            ordenesCache = ordenes;

            console.log("[cargarOrdenes] cantidad órdenes:", ordenes.length);

            renderizarOrdenes(ordenes);
            renderizarPaginacion(totalPages, currentPage);
            actualizarResumen(ordenes);
        })
        .catch((error) => {
            console.error("[cargarOrdenes] error:", error);

            const tbody = document.getElementById("tablaPedidos");
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7">Error al cargar pedidos</td>
                    </tr>
                `;
            }

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "No se pudieron cargar los pedidos"
            });
        });
}

async function cargarTotalesPedidos() {
    const pendientesEl = document.querySelector(".expenses h1");
    const pendientesSmall = document.querySelector(".expenses small");

    const procesoEl = document.querySelector(".income h1");
    const procesoSmall = document.querySelector(".income small");

    try {
        const res = await authFetch("/orden/totales");

        if (!res.ok) {
            throw new Error("Error al cargar totales de pedidos: " + res.status);
        }

        const data = await res.json();

        const pendientes = Number(data.pendientes || 0);
        const proceso = Number(data.proceso || 0);

        if (pendientesEl) {
            pendientesEl.textContent = pendientes;
        }

        if (pendientesSmall) {
            pendientesSmall.textContent = `${pendientes} pendiente${pendientes === 1 ? "" : "s"}`;
        }

        if (procesoEl) {
            procesoEl.textContent = proceso;
        }

        if (procesoSmall) {
            procesoSmall.textContent = `${proceso} en proceso`;
        }
    } catch (error) {
        console.error("[cargarTotalesPedidos] error:", error);

        if (pendientesEl) pendientesEl.textContent = "0";
        if (pendientesSmall) pendientesSmall.textContent = "No se pudo cargar";

        if (procesoEl) procesoEl.textContent = "0";
        if (procesoSmall) procesoSmall.textContent = "No se pudo cargar";
    }
}

async function cargarIngresosTotales() {
    const ingresosEl = document.querySelector(".orders h1");
    const ingresosSmall = document.querySelector(".orders small");

    if (!ingresosEl) return;

    try {
        const res = await authFetch("/orden/total?fechaInicio=&fechaFin=");

        if (!res.ok) {
            throw new Error("Error al cargar ingresos totales: " + res.status);
        }

        const data = await res.text();
        const total = Number(data);

        ingresosEl.textContent = formatearMoneda(isNaN(total) ? 0 : total);

        if (ingresosSmall) {
            ingresosSmall.textContent = "Total acumulado";
        }
    } catch (error) {
        console.error("[cargarIngresosTotales] error:", error);
        ingresosEl.textContent = formatearMoneda(0);

        if (ingresosSmall) {
            ingresosSmall.textContent = "No se pudo cargar";
        }
    }
}

function renderizarOrdenes(ordenes) {
    const tbody = document.getElementById("tablaPedidos");
    console.log("[renderizarOrdenes] tbody:", tbody);

    if (!tbody) {
        console.error("[renderizarOrdenes] No se encontró #tablaPedidos");
        return;
    }

    tbody.innerHTML = "";

    if (!ordenes || ordenes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7">No hay pedidos para mostrar</td>
            </tr>
        `;
        return;
    }

    ordenes.forEach((orden, index) => {
        console.log(`[renderizarOrdenes] orden #${index}:`, orden);

        const idOrden = orden.id_orden ?? "";
        const cliente = orden.usuario?.nombre ?? "Sin cliente";
        const fecha = formatearFecha(orden.fecha_creacion);
        const cantidadProductos = contarProductosOrden(orden.listaOrdenDetalle);
        const total = formatearMoneda(orden.total);
        const estado = orden.estado ?? "Sin estado";

        const fila = `
            <tr>
                <td>${escapeHtml(`ORD-${idOrden}`)}</td>
                <td>${escapeHtml(cliente)}</td>
                <td>${escapeHtml(fecha)}</td>
                <td>${escapeHtml(`${cantidadProductos} producto${cantidadProductos === 1 ? "" : "s"}`)}</td>
                <td>${escapeHtml(total)}</td>
                <td>${escapeHtml(estado)}</td>
                <td>
                    <a href="#" class="view-order" data-id="${idOrden}">
                        <span class="material-symbols-outlined">edit</span>
                    </a>
                </td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", fila);
    });

    tbody.querySelectorAll(".view-order").forEach((btn) => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();

            const id = this.dataset.id;
            const orden = ordenesCache.find((o) => String(o.id_orden) === String(id));

            console.log("[view-order] id:", id, "orden encontrada:", orden);

            if (orden) {
                abrirModalOrden(orden);
            }
        });
    });
}

function contarProductosOrden(listaOrdenDetalle) {
    if (!Array.isArray(listaOrdenDetalle)) return 0;
    return listaOrdenDetalle.length;
}

function abrirModalOrden(orden) {
    console.log("[abrirModalOrden] orden:", orden);

    ordenSeleccionadaActual = orden;

    const modal = document.getElementById("orderModal");
    if (!modal) return;

    const idInput = document.getElementById("pedidoId");
    const clienteInput = document.getElementById("pedidoCliente");
    const fechaInput = document.getElementById("pedidoFecha");
    const productosInput = document.getElementById("pedidoProductos");
    const totalInput = document.getElementById("pedidoTotal");
    const estadoSelect = document.getElementById("pedidoEstado");

    if (idInput) idInput.value = String(orden.id_orden ?? "");
    if (clienteInput) clienteInput.value = orden.usuario?.nombre ?? "Sin cliente";
    if (fechaInput) fechaInput.value = formatearFecha(orden.fecha_creacion);

    if (productosInput) {
        const totalProductos = contarProductosOrden(orden.listaOrdenDetalle);
        productosInput.value = `${totalProductos} producto${totalProductos === 1 ? "" : "s"}`;
    }

    if (totalInput) totalInput.value = formatearMoneda(orden.total);

    if (estadoSelect) {
        const valorNormalizado = normalizarEstadoParaSelect(orden.estado ?? "");
        estadoSelect.value = valorNormalizado;
    }

    modal.style.display = "flex";
}

function abrirModalProductos() {
    console.log("[abrirModalProductos] ordenSeleccionadaActual:", ordenSeleccionadaActual);

    if (!ordenSeleccionadaActual) {
        Swal.fire({
            icon: "warning",
            title: "Sin pedido",
            text: "No hay un pedido seleccionado"
        });
        return;
    }

    const modalProductos = document.getElementById("productosModal");
    const textarea = document.getElementById("productosDetalleTexto");

    if (!modalProductos || !textarea) {
        console.error("[abrirModalProductos] No se encontró el modal o el textarea");
        return;
    }

    textarea.value = construirTextoProductos(ordenSeleccionadaActual.listaOrdenDetalle);
    modalProductos.style.display = "flex";
}

function construirTextoProductos(listaOrdenDetalle) {
    if (!Array.isArray(listaOrdenDetalle) || listaOrdenDetalle.length === 0) {
        return "Este pedido no tiene productos en la lista.";
    }

    return listaOrdenDetalle.map((detalle, index) => {
        const nombre = detalle?.producto?.nombre || "Producto sin nombre";
        const cantidad = Number(detalle?.cantidad ?? 0);
        const precio = Number(detalle?.precio ?? 0);

        return [
            `Producto ${index + 1}`,
            `Nombre: ${nombre}`,
            `Cantidad: ${cantidad}`,
            `Precio: ${formatearMoneda(precio)}`,
            "------------------------------"
        ].join("\n");
    }).join("\n");
}

async function actualizarEstadoOrden() {
    const idOrden = document.getElementById("pedidoId")?.value?.trim();
    const nuevoEstado = document.getElementById("pedidoEstado")?.value?.trim();
    const modal = document.getElementById("orderModal");

    console.log("[actualizarEstadoOrden] idOrden:", idOrden);
    console.log("[actualizarEstadoOrden] nuevoEstado:", nuevoEstado);

    if (!idOrden) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se encontró el id de la orden"
        });
        return;
    }

    if (!nuevoEstado) {
        Swal.fire({
            icon: "warning",
            title: "Estado requerido",
            text: "Debes seleccionar un estado"
        });
        return;
    }

    const payload = {
        id_orden: Number(idOrden),
        estado: nuevoEstado
    };

    console.log("[actualizarEstadoOrden] payload:", payload);

    try {
        const res = await authFetch("/orden/editar", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const texto = await res.text();
        console.log("[actualizarEstadoOrden] status:", res.status);
        console.log("[actualizarEstadoOrden] respuesta texto:", texto);

        if (!res.ok) {
            throw new Error(texto || "No se pudo actualizar la orden");
        }

        if (modal) {
            modal.style.display = "none";
        }

        Swal.fire({
            icon: "success",
            title: "Orden actualizada",
            text: "El estado se guardó correctamente"
        });

        cargarOrdenes(paginaActual);
        cargarIngresosTotales();
        cargarTotalesPedidos();
    } catch (error) {
        console.error("[actualizarEstadoOrden] error:", error);

        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "No se pudo actualizar la orden"
        });
    }
}

function normalizarEstadoParaSelect(estado) {
    if (!estado) return "";

    const e = estado.toUpperCase().trim();

    if (e === "PENDIENTE") return "PENDIENTE";
    if (e === "EN PROCESO" || e === "EN_PROCESO") return "EN PROCESO";
    if (e === "ENTREGADO") return "ENTREGADO";
    if (e === "CANCELADO") return "CANCELADO";

    return estado;
}

function actualizarResumen(ordenes) {
    const ingresosSmall = document.querySelector(".orders small");
    const entregados = contarPorEstado(ordenes, "ENTREGADO");

    if (ingresosSmall) {
        ingresosSmall.textContent = `${entregados} entregado${entregados === 1 ? "" : "s"} en esta página`;
    }
}

function contarPorEstado(ordenes, estadoBuscado) {
    return (ordenes || []).filter((orden) => {
        return (orden.estado || "").toUpperCase().trim() === estadoBuscado;
    }).length;
}

function renderizarPaginacion(totalPages, currentPage) {
    let contenedor = document.getElementById("paginacionPedidos");

    if (!contenedor) {
        const recentOrders = document.querySelector(".recent-orders");
        if (!recentOrders) return;

        contenedor = document.createElement("div");
        contenedor.id = "paginacionPedidos";
        contenedor.className = "paginacion";

        const tabla = recentOrders.querySelector("table");

        if (tabla) {
            recentOrders.insertBefore(contenedor, tabla);
        } else {
            recentOrders.prepend(contenedor);
        }
    }

    contenedor.innerHTML = "";

    if (totalPages <= 1) return;

    if (currentPage > 0) {
        const prevBtn = document.createElement("button");
        prevBtn.type = "button";
        prevBtn.innerText = "←";
        prevBtn.classList.add("btn-pagina");
        prevBtn.addEventListener("click", () => cargarOrdenes(currentPage - 1));
        contenedor.appendChild(prevBtn);
    }

    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    if (currentPage <= 1) {
        endPage = Math.min(totalPages - 1, 4);
    }

    if (currentPage >= totalPages - 2) {
        startPage = Math.max(0, totalPages - 5);
    }

    for (let i = startPage; i <= endPage; i++) {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.innerText = i + 1;
        btn.classList.add("btn-pagina");

        if (i === currentPage) {
            btn.classList.add("active");
        }

        btn.addEventListener("click", () => cargarOrdenes(i));
        contenedor.appendChild(btn);
    }

    if (currentPage < totalPages - 1) {
        const nextBtn = document.createElement("button");
        nextBtn.type = "button";
        nextBtn.innerText = "→";
        nextBtn.classList.add("btn-pagina");
        nextBtn.addEventListener("click", () => cargarOrdenes(currentPage + 1));
        contenedor.appendChild(nextBtn);
    }
}

function formatearFecha(fecha) {
    if (!fecha) return "Fecha no disponible";

    const date = new Date(fecha);

    if (isNaN(date.getTime())) {
        return fecha;
    }

    return date.toLocaleString("es-CR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit"
    });
}

function formatearMoneda(valor) {
    const numero = Number(valor || 0);
    return `CRC ${numero.toLocaleString("es-CR")}`;
}

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}