function obtenerToken() {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
        console.error("Token inválido");
        Swal.fire({
            icon: "warning",
            title: "Sesión inválida",
            text: "Debes iniciar sesión nuevamente"
        }).then(() => {
            window.location.href = "/login.html";
        });
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

let paginaActualLogs = 0;
let totalPaginasLogs = 0;
let logsCache = [];

let filtroAccion = "";
let filtroFechaInicio = "";
let filtroFechaFin = "";

const EXPORT_LOGS_PDF_ENDPOINT = "/reportes/logs/pdf";

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) return;

    const accionFilter = document.getElementById("accionFilter");
    const fechaInicio = document.getElementById("fechaInicio");
    const fechaFin = document.getElementById("fechaFin");

    const openExportModalBtn = document.getElementById("openExportModal");
    const closeModalBtn = document.getElementById("closeModal");
    const exportModal = document.getElementById("exportarLogsModal");
    const exportLogsForm = document.getElementById("exportLogsForm");

    const fechaInicioModal = document.getElementById("fechaInicioModal");
    const fechaFinModal = document.getElementById("fechaFinModal");

    if (accionFilter) {
        accionFilter.addEventListener("change", function () {
            filtroAccion = this.value.trim();
            cargarLogs(0);
        });
    }

    if (fechaInicio) {
        fechaInicio.addEventListener("change", function () {
            filtroFechaInicio = this.value;
            cargarLogs(0);
        });
    }

    if (fechaFin) {
        fechaFin.addEventListener("change", function () {
            filtroFechaFin = this.value;
            cargarLogs(0);
        });
    }

    if (openExportModalBtn && exportModal) {
        openExportModalBtn.addEventListener("click", () => {
            if (fechaInicioModal) fechaInicioModal.value = filtroFechaInicio || "";
            if (fechaFinModal) fechaFinModal.value = filtroFechaFin || "";
            exportModal.style.display = "flex";
        });
    }

    if (closeModalBtn && exportModal) {
        closeModalBtn.addEventListener("click", () => {
            exportModal.style.display = "none";
        });
    }

    window.addEventListener("click", (event) => {
        if (event.target === exportModal) {
            exportModal.style.display = "none";
        }
    });

    if (exportLogsForm) {
        exportLogsForm.addEventListener("submit", (e) => {
            e.preventDefault();

            const fechaIni = fechaInicioModal?.value || "";
            const fechaFinVal = fechaFinModal?.value || "";

            exportarLogsPDF(fechaIni, fechaFinVal);
        });
    }

    cargarLogs(0);
    cargarResumenAuditoria();
});

function construirUrlLogs(page = 0) {
    const params = new URLSearchParams();
    params.append("page", page);
    params.append("size", 10);

    if (filtroAccion) {
        params.append("accion", filtroAccion);
    }

    if (filtroFechaInicio) {
        params.append("fechaInicio", filtroFechaInicio);
    }

    if (filtroFechaFin) {
        params.append("fechaFin", filtroFechaFin);
    }

    return `/log/filtrar?${params.toString()}`;
}

function cargarLogs(page = 0) {
    const url = construirUrlLogs(page);

    authFetch(url)
        .then(async (res) => {
            if (!res.ok) {
                let mensaje = "Error al cargar logs";
                try {
                    const errorData = await res.json();
                    mensaje = errorData.message || mensaje;
                } catch (_) {}
                throw new Error(`${mensaje} (${res.status})`);
            }
            return res.json();
        })
        .then((data) => {
            console.log("RESPUESTA LOGS:", data);

            const logs = data.content || [];
            const currentPage = data.number ?? data.page?.number ?? 0;
            const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

            paginaActualLogs = currentPage;
            totalPaginasLogs = totalPages;
            logsCache = logs;

            renderizarLogs(logs);
            renderizarPaginacionLogs(totalPages, currentPage);
        })
        .catch((error) => {
            console.error("Error cargando logs:", error);

            const tbody = document.getElementById("tablaAuditoria");
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="4">Error al cargar logs</td>
                    </tr>
                `;
            }

            const paginacion = document.getElementById("paginacionLogs");
            if (paginacion) {
                paginacion.innerHTML = "";
            }

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "No se pudieron cargar los logs"
            });
        });
}

async function cargarResumenAuditoria() {
    const totalEventosEl = document.querySelector(".sales h1");
    const hoyEl = document.querySelector(".income h1");

    const totalEventosSmall = document.querySelector(".sales small");
    const hoySmall = document.querySelector(".income small");

    try {
        const res = await authFetch("/log/totales");

        if (!res.ok) {
            throw new Error("Error al cargar resumen de auditoría: " + res.status);
        }

        const data = await res.json();

        const total = Number(data.Total || 0);
        const hoy = Number(data.Hoy || 0);

        if (totalEventosEl) {
            totalEventosEl.textContent = total;
        }

        if (hoyEl) {
            hoyEl.textContent = hoy;
        }

        if (totalEventosSmall) {
            totalEventosSmall.textContent = "Registrados en el sistema";
        }

        if (hoySmall) {
            hoySmall.textContent = "Eventos de hoy";
        }
    } catch (error) {
        console.error("Error cargando resumen de auditoría:", error);

        if (totalEventosEl) {
            totalEventosEl.textContent = "0";
        }

        if (hoyEl) {
            hoyEl.textContent = "0";
        }

        if (totalEventosSmall) {
            totalEventosSmall.textContent = "No se pudo cargar";
        }

        if (hoySmall) {
            hoySmall.textContent = "No se pudo cargar";
        }
    }
}

function renderizarLogs(logs) {
    const tbody = document.getElementById("tablaAuditoria");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!logs || logs.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="4">No hay eventos para mostrar</td>
            </tr>
        `;
        return;
    }

    logs.forEach((log) => {
        const fechaHora = formatearFechaHora(log.fecha_actualizacion);
        const usuario = obtenerNombreUsuarioLog(log);
        const accion = log.accion || "Sin acción";
        const detalle = log.detalle || "Sin detalle";

        const fila = `
            <tr>
                <td>${escapeHtml(fechaHora)}</td>
                <td>${escapeHtml(usuario)}</td>
                <td>${escapeHtml(accion)}</td>
                <td>${escapeHtml(detalle)}</td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", fila);
    });
}

function renderizarPaginacionLogs(totalPages, currentPage) {
    const contenedor = document.getElementById("paginacionLogs");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (totalPages <= 1) {
        return;
    }

    if (currentPage > 0) {
        const prevBtn = document.createElement("button");
        prevBtn.type = "button";
        prevBtn.innerText = "←";
        prevBtn.classList.add("btn-pagina");
        prevBtn.addEventListener("click", () => cargarLogs(currentPage - 1));
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

        btn.addEventListener("click", () => cargarLogs(i));
        contenedor.appendChild(btn);
    }

    if (currentPage < totalPages - 1) {
        const nextBtn = document.createElement("button");
        nextBtn.type = "button";
        nextBtn.innerText = "→";
        nextBtn.classList.add("btn-pagina");
        nextBtn.addEventListener("click", () => cargarLogs(currentPage + 1));
        contenedor.appendChild(nextBtn);
    }
}

function obtenerNombreUsuarioLog(log) {
    if (typeof log.usuario === "string") return log.usuario;
    if (log.usuario && typeof log.usuario.nombre === "string") return log.usuario.nombre;
    if (typeof log.nombreUsuario === "string") return log.nombreUsuario;
    return "Sin usuario";
}

function formatearFechaHora(fecha) {
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
        minute: "2-digit",
        second: "2-digit"
    });
}

function exportarLogsPDF(fechaInicio, fechaFin) {
    const params = new URLSearchParams();

    if (fechaInicio) {
        params.append("fechaInicio", fechaInicio);
    }

    if (fechaFin) {
        params.append("fechaFin", fechaFin);
    }

    const url = `${EXPORT_LOGS_PDF_ENDPOINT}?${params.toString()}`;
    const token = obtenerToken();

    if (!token) return;

    descargarPdfConAuth(url, token);
}

function descargarPdfConAuth(url, token) {
    fetch(url, {
        method: "GET",
        headers: {
            Authorization: "Bearer " + token
        }
    })
        .then(async (res) => {
            if (!res.ok) {
                let mensaje = "No se pudo exportar el PDF";
                try {
                    const errorData = await res.json();
                    mensaje = errorData.message || mensaje;
                } catch (_) {}
                throw new Error(mensaje);
            }

            return res.blob();
        })
        .then((blob) => {
            const blobUrl = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = blobUrl;
            a.download = "logs_auditoria.pdf";
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(blobUrl);

            const modal = document.getElementById("exportarLogsModal");
            if (modal) {
                modal.style.display = "none";
            }

            Swal.fire({
                icon: "success",
                title: "PDF generado",
                text: "La exportación se realizó correctamente"
            });
        })
        .catch((error) => {
            console.error("Error exportando PDF:", error);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "No se pudo exportar el PDF"
            });
        });
}

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}