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

let reporteSeleccionado = null;

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) return;

    configurarSeleccionReporte();
    configurarBotonesExportacion();
});

function configurarSeleccionReporte() {
    const cardsReporte = document.querySelectorAll(".reporte-grid .category-card");

    cardsReporte.forEach((card) => {
        card.style.cursor = "pointer";

        card.addEventListener("click", () => {
            cardsReporte.forEach((c) => c.classList.remove("selected-report"));
            card.classList.add("selected-report");

            reporteSeleccionado = card.dataset.reporte || null;

            console.log("Reporte seleccionado:", reporteSeleccionado);
        });
    });
}

function configurarBotonesExportacion() {
    const btnPdf = document.getElementById("btnExportPdf");
    const btnExcel = document.getElementById("btnExportExcel");

    if (btnPdf) {
        btnPdf.style.cursor = "pointer";
        btnPdf.addEventListener("click", exportarSegunSeleccionPDF);
    }

    if (btnExcel) {
        btnExcel.style.cursor = "pointer";
        btnExcel.addEventListener("click", exportarSegunSeleccionExcel);
    }
}

function obtenerFechasFiltro() {
    const fechaInicio = document.getElementById("fechaInicio")?.value || "";
    const fechaFin = document.getElementById("fechaFin")?.value || "";

    return { fechaInicio, fechaFin };
}

async function exportarSegunSeleccionPDF() {
    if (!reporteSeleccionado) {
        Swal.fire({
            icon: "info",
            title: "Selecciona un reporte",
            text: "Debes seleccionar uno de los reportes antes de exportar"
        });
        return;
    }

    switch (reporteSeleccionado) {
        case "inventario":
            await exportarInventarioPDF();
            break;

        case "movimientos": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendPDF(
                "/reportes/movimientos/pdf",
                "reporte_movimientos.pdf",
                fechaInicio,
                fechaFin
            );
            break;
        }

        case "ventas": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendPDF(
                "/reportes/ventas/pdf",
                "reporte_ventas.pdf",
                fechaInicio,
                fechaFin
            );
            break;
        }

        case "pedidos": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendPDF(
                "/reportes/pedidos/pdf",
                "reporte_pedidos.pdf",
                fechaInicio,
                fechaFin
            );
            break;
        }

        default:
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Tipo de reporte no reconocido"
            });
    }
}

async function exportarSegunSeleccionExcel() {
    if (!reporteSeleccionado) {
        Swal.fire({
            icon: "info",
            title: "Selecciona un reporte",
            text: "Debes seleccionar uno de los reportes antes de exportar"
        });
        return;
    }

    switch (reporteSeleccionado) {
        case "inventario":
            await exportarInventarioExcel();
            break;

        case "movimientos": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendExcel(
                "/reportes/movimientos/excel",
                "reporte_movimientos.xlsx",
                fechaInicio,
                fechaFin
            );
            break;
        }

        case "ventas": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendExcel(
                "/reportes/ventas/excel",
                "reporte_ventas.xlsx",
                fechaInicio,
                fechaFin
            );
            break;
        }

        case "pedidos": {
            const { fechaInicio, fechaFin } = obtenerFechasFiltro();
            await descargarReporteBackendExcel(
                "/reportes/pedidos/excel",
                "reporte_pedidos.xlsx",
                fechaInicio,
                fechaFin
            );
            break;
        }

        default:
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Tipo de reporte no reconocido"
            });
    }
}

async function obtenerDatosInventarioReporte() {
    const res = await authFetch("/inventario/reporte");

    if (!res.ok) {
        let mensaje = "No se pudo obtener el reporte de inventario";
        try {
            const errorData = await res.json();
            mensaje = errorData.message || mensaje;
        } catch (_) {}
        throw new Error(mensaje);
    }

    const productos = await res.json();

    return (productos || []).map((producto) => ({
        nombre: producto.nombre ?? "Sin nombre",
        stock_actual: producto.stock_actual ?? 0,
        precio: producto.precio ?? 0
    }));
}

async function exportarInventarioPDF() {
    try {
        const datos = await obtenerDatosInventarioReporte();

        if (!datos.length) {
            Swal.fire({
                icon: "info",
                title: "Sin datos",
                text: "No hay productos para exportar"
            });
            return;
        }

        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        doc.setFontSize(16);
        doc.text("Reporte de Inventario", 14, 15);

        doc.setFontSize(10);
        doc.text(`Generado: ${new Date().toLocaleString("es-CR")}`, 14, 22);

        const body = datos.map((item) => [
            item.nombre,
            item.stock_actual,
            `${Number(item.precio).toLocaleString("es-CR")}`
        ]);

        doc.autoTable({
            startY: 28,
            head: [["Nombre", "Stock actual", "Precio"]],
            body,
            styles: {
                fontSize: 10
            },
            headStyles: {
                fillColor: [60, 60, 60]
            }
        });

        doc.save("reporte_inventario.pdf");

        Swal.fire({
            icon: "success",
            title: "PDF generado",
            text: "El reporte PDF se descargó correctamente"
        });
    } catch (error) {
        console.error("Error exportando PDF:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "No se pudo exportar el PDF"
        });
    }
}

async function exportarInventarioExcel() {
    try {
        const datos = await obtenerDatosInventarioReporte();

        if (!datos.length) {
            Swal.fire({
                icon: "info",
                title: "Sin datos",
                text: "No hay productos para exportar"
            });
            return;
        }

        const datosExcel = datos.map((item) => ({
            Nombre: item.nombre,
            "Stock actual": item.stock_actual,
            Precio: item.precio
        }));

        const worksheet = XLSX.utils.json_to_sheet(datosExcel);
        const workbook = XLSX.utils.book_new();

        XLSX.utils.book_append_sheet(workbook, worksheet, "Inventario");
        XLSX.writeFile(workbook, "reporte_inventario.xlsx");

        Swal.fire({
            icon: "success",
            title: "Excel generado",
            text: "El reporte Excel se descargó correctamente"
        });
    } catch (error) {
        console.error("Error exportando Excel:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "No se pudo exportar el Excel"
        });
    }
}

async function descargarReporteBackendPDF(endpoint, nombreArchivo, fechaInicio, fechaFin) {
    try {
        const url = construirUrlReporteConFechas(endpoint, fechaInicio, fechaFin);
        const token = obtenerToken();

        if (!token) return;

        const res = await fetch(url, {
            method: "GET",
            headers: {
                Authorization: "Bearer " + token
            }
        });

        if (!res.ok) {
            let mensaje = "No se pudo exportar el PDF";
            try {
                const errorData = await res.json();
                mensaje = errorData.message || mensaje;
            } catch (_) {}
            throw new Error(mensaje);
        }

        const blob = await res.blob();
        descargarBlob(blob, nombreArchivo);

        Swal.fire({
            icon: "success",
            title: "PDF generado",
            text: "El reporte PDF se descargó correctamente"
        });
    } catch (error) {
        console.error("Error exportando PDF backend:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "No se pudo exportar el PDF"
        });
    }
}

async function descargarReporteBackendExcel(endpoint, nombreArchivo, fechaInicio, fechaFin) {
    try {
        const url = construirUrlReporteConFechas(endpoint, fechaInicio, fechaFin);
        const token = obtenerToken();

        if (!token) return;

        const res = await fetch(url, {
            method: "GET",
            headers: {
                Authorization: "Bearer " + token
            }
        });

        if (!res.ok) {
            let mensaje = "No se pudo exportar el Excel";
            try {
                const errorData = await res.json();
                mensaje = errorData.message || mensaje;
            } catch (_) {}
            throw new Error(mensaje);
        }

        const blob = await res.blob();
        descargarBlob(blob, nombreArchivo);

        Swal.fire({
            icon: "success",
            title: "Excel generado",
            text: "El reporte Excel se descargó correctamente"
        });
    } catch (error) {
        console.error("Error exportando Excel backend:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "No se pudo exportar el Excel"
        });
    }
}

function construirUrlReporteConFechas(endpoint, fechaInicio, fechaFin) {
    const params = new URLSearchParams();

    if (fechaInicio) {
        params.append("fechaInicio", fechaInicio);
    }

    if (fechaFin) {
        params.append("fechaFin", fechaFin);
    }

    const query = params.toString();
    return query ? `${endpoint}?${query}` : endpoint;
}

function descargarBlob(blob, nombreArchivo) {
    const blobUrl = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = blobUrl;
    a.download = nombreArchivo;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(blobUrl);
}