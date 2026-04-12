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
        const token = localStorage.getItem("token");

        if (!token) {
            window.location.href = "/login.html";
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

    let paginaActualMovimientos = 0;
    let totalPaginasMovimientos = 0;
    let debounceTimerMovimientos = null;

    let filtroProducto = "";
    let filtroTipo = "";
    let filtroFechaInicio = "";
    let filtroFechaFin = "";

    document.addEventListener("DOMContentLoaded", () => {
        const token = obtenerToken();
        if (!token) {
            window.location.replace("/login.html");
            return;
        }

        const searchProducto = document.getElementById("searchProducto");
        const tipoMovimiento = document.getElementById("tipoMovimiento");
        const fechaInicio = document.getElementById("fechaInicio");
        const fechaFin = document.getElementById("fechaFin");

        if (searchProducto) {
            searchProducto.addEventListener("input", function () {
                clearTimeout(debounceTimerMovimientos);

                debounceTimerMovimientos = setTimeout(() => {
                    filtroProducto = this.value.trim();
                    cargarMovimientos(0);
                }, 400);
            });
        }

        if (tipoMovimiento) {
            tipoMovimiento.addEventListener("change", function () {
                filtroTipo = this.value;
                cargarMovimientos(0);
            });
        }

        if (fechaInicio) {
            fechaInicio.addEventListener("change", function () {
                filtroFechaInicio = this.value;
                cargarMovimientos(0);
            });
        }

        if (fechaFin) {
            fechaFin.addEventListener("change", function () {
                filtroFechaFin = this.value;
                cargarMovimientos(0);
            });
        }

        cargarMovimientos(0);
    });

    function construirUrlMovimientos(page = 0) {
        const params = new URLSearchParams();

        params.append("page", page);
        params.append("size", 10);

        if (filtroProducto.trim() !== "") {
            params.append("producto", filtroProducto.trim());
        }

        if (filtroTipo.trim() !== "") {
            params.append("tipo", filtroTipo.trim());
        }

        if (filtroFechaInicio) {
            params.append("fechaInicio", filtroFechaInicio);
        }

        if (filtroFechaFin) {
            params.append("fechaFin", filtroFechaFin);
        }

        return `/movimiento/traer?${params.toString()}`;
    }

    function cargarMovimientos(page = 0) {
        const url = construirUrlMovimientos(page);

        console.log("URL movimientos:", url);

        authFetch(url)
            .then(res => {
                console.log("STATUS MOVIMIENTOS:", res.status);

                if (!res.ok) {
                    throw new Error("Error al cargar movimientos: " + res.status);
                }
                return res.json();
            })
            .then(data => {
                console.log("RESPUESTA MOVIMIENTOS:", data);

                const movimientos = data.content || [];
                const currentPage = data.number ?? data.page?.number ?? 0;
                const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

                console.log("MOVIMIENTOS:", movimientos);
                console.log("PAGINA ACTUAL:", currentPage);
                console.log("TOTAL PAGINAS:", totalPages);

                paginaActualMovimientos = currentPage;
                totalPaginasMovimientos = totalPages;

                renderizarMovimientos(movimientos);
                renderizarPaginacionMovimientos(totalPages, currentPage);
            })
            .catch(err => {
                console.error(err);

                const tbody = document.getElementById("tablaMovimientos");
                if (tbody) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="5">Error al cargar movimientos</td>
                        </tr>
                    `;
                }

                const contenedor = document.getElementById("paginacionMovimientos");
                if (contenedor) {
                    contenedor.innerHTML = "";
                }
            });
    }

    function renderizarMovimientos(movimientos) {
        const tbody = document.getElementById("tablaMovimientos");
        if (!tbody) return;

        tbody.innerHTML = "";

        if (!movimientos || movimientos.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5">No hay movimientos para mostrar</td>
                </tr>
            `;
            return;
        }

        movimientos.forEach(mov => {
            const fecha = formatearFecha(mov.fecha_movimiento);
            const tipo = mov.tipo_movimiento || "Sin tipo";
            const producto = mov.producto?.nombre || "Sin producto";
            const cantidad = mov.cantidad ?? 0;
            const usuario = mov.usuario?.nombre || "Sin usuario";

            const claseTipo =
                tipo === "ENTRADA" ? "success" :
                    tipo === "SALIDA" ? "danger" :
                        "warning";

            const fila = `
                <tr>
                    <td>${fecha}</td>
                    <td class="${claseTipo}">${tipo}</td>
                    <td>${producto}</td>
                    <td>${cantidad}</td>
                    <td>${usuario}</td>
                </tr>
            `;

            tbody.insertAdjacentHTML("beforeend", fila);
        });
    }

    function renderizarPaginacionMovimientos(totalPages, currentPage) {
        const contenedor = document.getElementById("paginacionMovimientos");
        if (!contenedor) return;

        contenedor.innerHTML = "";

        for (let i = 0; i < totalPages; i++) {
            const btn = document.createElement("button");
            btn.innerText = i + 1;
            btn.classList.add("btn-pagina");

            if (i === currentPage) {
                btn.classList.add("active");
            }

            btn.addEventListener("click", () => {
                cargarMovimientos(i);
            });

            contenedor.appendChild(btn);
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
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        });
    }