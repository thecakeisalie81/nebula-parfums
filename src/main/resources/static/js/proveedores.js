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

let paginaActualProveedores = 0;
let totalPaginasProveedores = 0;
let proveedoresPaginaActual = [];

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) {
        window.location.replace("/login.html");
        return;
    }

    cargarResumenProveedores();
    cargarProveedoresPaginados(0);
    configurarModalProveedor();
    configurarCrearProveedor();
    configurarEditarProveedor();
});

async function cargarResumenProveedores() {
    try {
        let response = await authFetch("/proveedor/total");
        let data = await response.text();
        const totalProveedores = Number(data.trim());

        response = await authFetch("/producto/totalproductos");
        data = await response.text();
        const totalProductos = Number(data.trim());

        document.querySelector("#totalProveedores").innerHTML = totalProveedores;
        document.querySelector("#totalProductos").innerHTML = totalProductos;
    } catch (error) {
        console.error("Error cargando resumen de proveedores:", error);
    }
}

function cargarProveedoresPaginados(page = 0) {
    authFetch(`/proveedor/traer?page=${page}&size=10`)
        .then(res => {
            if (!res.ok) {
                throw new Error("Error al cargar proveedores: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            const proveedores = data.content || [];
            const currentPage = data.number ?? data.page?.number ?? 0;
            const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

            proveedoresPaginaActual = proveedores;
            paginaActualProveedores = currentPage;
            totalPaginasProveedores = totalPages;

            renderizarProveedores(proveedores);
            renderizarPaginacionProveedores(totalPages, currentPage);
        })
        .catch(error => {
            console.error(error);

            const tbody = document.getElementById("tablaProveedores");
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="6">Error al cargar proveedores</td>
                    </tr>
                `;
            }

            const paginacion = document.getElementById("paginacionProveedores");
            if (paginacion) {
                paginacion.innerHTML = "";
            }
        });
}

function renderizarProveedores(proveedores) {
    const tbody = document.getElementById("tablaProveedores");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!proveedores || proveedores.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6">No hay proveedores para mostrar</td>
            </tr>
        `;
        return;
    }

    proveedores.forEach(proveedor => {
        const id = proveedor.id_proveedor ?? "";
        const nombre = proveedor.nombre ?? "Sin nombre";
        const contacto = proveedor.contacto ?? "Sin contacto";
        const email = proveedor.email ?? "Sin email";
        const telefono = proveedor.telefono ?? "Sin teléfono";

        const fila = `
            <tr>
                <td>${nombre}</td>
                <td>${contacto}</td>
                <td>${email}</td>
                <td>${telefono}</td>
                <td>
                    <a href="javascript:void(0)" onclick="abrirModalEditarProveedor(${id})">Editar</a>
                </td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", fila);
    });
}

function renderizarPaginacionProveedores(totalPages, currentPage) {
    const contenedor = document.getElementById("paginacionProveedores");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (totalPages <= 1) return;

    if (currentPage > 0) {
        const prevBtn = document.createElement("button");
        prevBtn.innerText = "←";
        prevBtn.classList.add("btn-pagina");
        prevBtn.addEventListener("click", () => {
            cargarProveedoresPaginados(currentPage - 1);
        });
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
        btn.innerText = i + 1;
        btn.classList.add("btn-pagina");

        if (i === currentPage) {
            btn.classList.add("active");
        }

        btn.addEventListener("click", () => {
            cargarProveedoresPaginados(i);
        });

        contenedor.appendChild(btn);
    }

    if (currentPage < totalPages - 1) {
        const nextBtn = document.createElement("button");
        nextBtn.innerText = "→";
        nextBtn.classList.add("btn-pagina");
        nextBtn.addEventListener("click", () => {
            cargarProveedoresPaginados(currentPage + 1);
        });
        contenedor.appendChild(nextBtn);
    }
}

function configurarModalProveedor() {
    const modal = document.getElementById("providerModal");
    const closeBtn = document.getElementById("closeProviderModal");

    closeBtn?.addEventListener("click", () => {
        modal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
}

function abrirModalEditarProveedor(idProveedor) {
    const modal = document.getElementById("providerModal");

    const proveedor = proveedoresPaginaActual.find(
        p => Number(p.id_proveedor) === Number(idProveedor)
    );

    if (!proveedor) {
        console.error("Proveedor no encontrado en la página actual");
        return;
    }

    document.getElementById("editProvId").value = proveedor.id_proveedor ?? "";
    document.getElementById("editProvNombre").value = proveedor.nombre ?? "";
    document.getElementById("editProvContacto").value = proveedor.contacto ?? "";
    document.getElementById("editProvEmail").value = proveedor.email ?? "";
    document.getElementById("editProvTelefono").value = proveedor.telefono ?? "";

    modal.style.display = "flex";
}

function configurarEditarProveedor() {
    const form = document.getElementById("editProviderForm");
    if (!form) return;

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const id = document.getElementById("editProvId").value;
        const nombre = document.getElementById("editProvNombre").value.trim();
        const contacto = document.getElementById("editProvContacto").value.trim();
        const email = document.getElementById("editProvEmail").value.trim();
        const telefono = document.getElementById("editProvTelefono").value.trim();

        authFetch("/proveedor/editar", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id_proveedor: id,
                nombre,
                contacto,
                email,
                telefono
            })
        })
            .then(() => {
                document.getElementById("providerModal").style.display = "none";

                Swal.fire({
                    icon: "success",
                    title: "Proveedor actualizado",
                    text: "Los cambios se guardaron correctamente"
                });

                cargarProveedoresPaginados(paginaActualProveedores);
                cargarResumenProveedores();
            })
            .catch(error => {
                console.error(error);

                Swal.fire({
                    icon: "error",
                    title: "Error al editar proveedor",
                    text: "No se pudieron guardar los cambios"
                });
            });
    });
}

function configurarCrearProveedor() {
    const form = document.getElementById("providerForm");
    if (!form) return;

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const nombre = document.getElementById("provNombre").value.trim();
        const contacto = document.getElementById("provContacto").value.trim();
        const email = document.getElementById("provEmail").value.trim();
        const telefono = document.getElementById("provTelefono").value.trim();

        // Asunción: /proveedor/crear recibe JSON.
        authFetch("/proveedor/crear", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nombre,
                contacto,
                email,
                telefono
            })
        })
            .then(() => {
                form.reset();

                Swal.fire({
                    icon: "success",
                    title: "Proveedor creado",
                    text: "Se guardó correctamente"
                });

                cargarProveedoresPaginados(0);
                cargarResumenProveedores();
            })
            .catch(error => {
                console.error(error);

                Swal.fire({
                    icon: "error",
                    title: "Error al crear proveedor",
                    text: "No se pudo guardar"
                });
            });
    });
}