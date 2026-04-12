function obtenerToken() {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
        console.error("Token inválido");
        window.location.href = "/login.html";
        return null;
    }

    return token;
}


async function cargarCuadros() {
    let response = await authFetch("/producto/totalproductos");
    let data = await response.text();   // aquí llega "28"
    const total = Number(data.trim());    // convertir a número
    document.querySelector("#totalProductos").innerHTML = total;

    response = await authFetch("/producto/nostock");
    data = await response.text();
    const noStock = Number(data.trim());
    document.querySelector("#noStock").innerHTML = noStock;
}

document.addEventListener("DOMContentLoaded", () => {
    cargarCuadros();

});

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

function cargarProductosBajoStock() {
    const contenedor = document.getElementById("stockBajoList");

    if (!contenedor) return;

    authFetch("/producto/bajostock")
        .then(res => {
            if (!res.ok) {
                throw new Error("Error al cargar productos con bajo stock: " + res.status);
            }
            return res.json();
        })
        .then(productos => {
            contenedor.innerHTML = "";

            if (!productos || productos.length === 0) {
                contenedor.innerHTML = `
                    <div class="item online">
                        <div class="right">
                            <div class="info">
                                <h3>No hay productos con stock bajo</h3>
                            </div>
                        </div>
                    </div>
                `;
                return;
            }

            productos.forEach(producto => {
                const item = `
                    <div class="item online">
                        <div class="icon">
                            <span class="material-symbols-outlined">warning</span>
                        </div>
                        <div class="right">
                            <div class="info">
                                <h3>${producto.nombre}</h3>
                                <small class="text-muted">Solo quedan</small>
                            </div>
                            <h5 class="danger">${producto.stock_actual} unidades</h5>
                        </div>
                    </div>
                `;

                contenedor.insertAdjacentHTML("beforeend", item);
            });
        })
        .catch(err => {
            console.error(err);
            contenedor.innerHTML = `
                <div class="item online">
                    <div class="right">
                        <div class="info">
                            <h3>Error al cargar productos</h3>
                        </div>
                    </div>
                </div>
            `;
        });
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


function cargarUltimosMovimientos() {
    const contenedor = document.getElementById("ultimosMovimientosList");

    if (!contenedor) return;

    authFetch("/movimiento/ultimos")
        .then(res => {
            if (!res.ok) {
                throw new Error("Error al cargar movimientos: " + res.status);
            }
            return res.json();
        })
        .then(movimientos => {
            contenedor.innerHTML = "";

            if (!movimientos || movimientos.length === 0) {
                contenedor.innerHTML = `
                    <div class="update">
                        <div class="message">
                            <p>No hay movimientos recientes</p>
                            <small class="text-muted">Sin datos</small>
                        </div>
                    </div>
                `;
                return;
            }

            movimientos.forEach(movimiento => {
                const empleado = movimiento.usuario?.nombre || "Empleado no disponible";
                const producto = movimiento.producto?.nombre || "Producto no disponible";
                const tipo = movimiento.tipo_movimiento || "Movimiento";
                const fecha = formatearFecha(movimiento.fecha_movimiento);

                const item = `
                    <div class="update">
                        <div class="message">
                            <p>
                                <b>${empleado}</b> realizó un movimiento de 
                                <b>${tipo}</b> en <b>${producto}</b>
                            </p>
                            <small class="text-muted">${fecha}</small>
                        </div>
                    </div>
                `;

                contenedor.insertAdjacentHTML("beforeend", item);
            });
        })
        .catch(err => {
            console.error(err);
            contenedor.innerHTML = `
                <div class="update">
                    <div class="message">
                        <p>Error al cargar movimientos recientes</p>
                        <small class="text-muted">Intenta recargar</small>
                    </div>
                </div>
            `;
        });
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

function cargarOrdenesRecientes() {
    const tbody = document.getElementById("ordenesRecientesBody");

    if (!tbody) return;

    authFetch("/orden/recientes")
        .then(res => {
            if (!res.ok) {
                throw new Error("Error al cargar órdenes recientes: " + res.status);
            }
            return res.json();
        })
        .then(ordenes => {
            tbody.innerHTML = "";

            if (!ordenes || ordenes.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5">No hay órdenes recientes</td>
                    </tr>
                `;
                return;
            }

            ordenes.forEach(orden => {
                const fecha = formatearFecha(orden.fecha_creacion || orden.fecha);
                const total = orden.total ?? "Sin total";
                const usuario = orden.usuario?.nombre || "Sin usuario";
                const correo = orden.usuario?.email || "Sin correo";
                const telefono = orden.direccion?.telefono || "Sin teléfono";

                const fila = `
                    <tr>
                        <td>${fecha}</td>
                        <td>₡${total}</td>
                        <td>${usuario}</td>
                        <td>${correo}</td>
                        <td>${telefono}</td>
                    </tr>
                `;

                tbody.insertAdjacentHTML("beforeend", fila);
            });
        })
        .catch(err => {
            console.error(err);
            tbody.innerHTML = `
                <tr>
                    <td colspan="5">Error al cargar órdenes recientes</td>
                </tr>
            `;
        });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarProductosBajoStock();
    cargarOrdenesRecientes();
    cargarUltimosMovimientos();
});