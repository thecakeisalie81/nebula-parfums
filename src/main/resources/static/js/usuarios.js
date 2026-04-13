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

let paginaActual = 0;
let totalPaginas = 0;
let totalElementos = 0;
let usuariosCache = [];
let debounceTimer = null;
let terminoBusqueda = "";

document.addEventListener("DOMContentLoaded", () => {
    const token = obtenerToken();
    if (!token) return;

    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const closeEditModalBtn = document.getElementById("closeEditModal");
    const userModal = document.getElementById("userModal");
    const editUserModal = document.getElementById("editUserModal");
    const searchInput = document.getElementById("searchInput");

    if (openModalBtn) {
        openModalBtn.addEventListener("click", () => {
            document.getElementById("userForm")?.reset();

            const rolSelect = document.getElementById("rol");
            if (rolSelect) {
                rolSelect.value = "ROLE_EMPLEADO";
            }

            userModal.style.display = "flex";
        });
    }

    if (closeModalBtn) {
        closeModalBtn.addEventListener("click", () => {
            userModal.style.display = "none";
        });
    }

    if (closeEditModalBtn) {
        closeEditModalBtn.addEventListener("click", () => {
            editUserModal.style.display = "none";
        });
    }

    window.addEventListener("click", (event) => {
        if (event.target === userModal) {
            userModal.style.display = "none";
        }

        if (event.target === editUserModal) {
            editUserModal.style.display = "none";
        }
    });

    if (searchInput) {
        searchInput.addEventListener("input", function () {
            clearTimeout(debounceTimer);

            debounceTimer = setTimeout(() => {
                terminoBusqueda = this.value.trim();
                cargarUsuarios(0);
            }, 300);
        });
    }

    configurarFormularioCrear();
    configurarFormularioEditar();

    cargarUsuarios(0);
    cargarTotalesResumen();
});

function construirUrlUsuarios(page = 0) {
    const params = new URLSearchParams();
    params.append("page", page);
    params.append("size", 10);

    if (terminoBusqueda) {
        params.append("nombre", terminoBusqueda);
    }

    return `/usuario/traer?${params.toString()}`;
}

function cargarUsuarios(page = 0) {
    const url = construirUrlUsuarios(page);

    authFetch(url)
        .then(async (res) => {
            if (!res.ok) {
                let mensaje = "Error al cargar usuarios";
                try {
                    const errorData = await res.json();
                    mensaje = errorData.message || mensaje;
                } catch (_) {}
                throw new Error(`${mensaje} (${res.status})`);
            }
            return res.json();
        })
        .then((data) => {
            console.log("RESPUESTA USUARIOS:", data);

            const usuarios = data.content || [];
            const currentPage = data.page?.number ?? 0;
            const totalPages = data.page?.totalPages ?? 0;
            const totalElements = data.page?.totalElements ?? usuarios.length;

            console.log("Paginación:", {
                terminoBusqueda,
                currentPage,
                totalPages,
                totalElements,
                cantidadUsuariosRecibidos: usuarios.length
            });

            paginaActual = currentPage;
            totalPaginas = totalPages;
            totalElementos = totalElements;
            usuariosCache = usuarios;

            renderizarUsuarios(usuarios);
            renderizarPaginacion(totalPages, currentPage);
        })
        .catch((error) => {
            console.error("Error cargando usuarios:", error);

            const tbody = document.getElementById("tablaUsuarios");
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="6">Error al cargar usuarios</td>
                    </tr>
                `;
            }

            const paginacion = document.getElementById("paginacion");
            if (paginacion) {
                paginacion.innerHTML = "";
            }

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "No se pudieron cargar los usuarios"
            });
        });
}

function cargarTotalesResumen() {
    Promise.all([
        authFetch("/usuario/total").then(async (res) => {
            if (!res.ok) throw new Error("No se pudo cargar el total de usuarios");
            return res.json();
        }),
        authFetch("/usuario/activos").then(async (res) => {
            if (!res.ok) throw new Error("No se pudo cargar el total de activos");
            return res.json();
        })
    ])
        .then(([totalUsuarios, totalActivos]) => {
            actualizarTotalUsuarios(totalUsuarios);
            actualizarTotalActivos(totalActivos);
        })
        .catch((error) => {
            console.error("Error cargando resumen:", error);
        });
}

function actualizarTotalUsuarios(total) {
    const elemento = document.getElementById("totalUsers");
    if (elemento) {
        elemento.textContent = Number.isFinite(Number(total)) ? total : 0;
    }
}

function actualizarTotalActivos(total) {
    const elemento = document.getElementById("totalActivos");
    if (elemento) {
        elemento.textContent = Number.isFinite(Number(total)) ? total : 0;
    }
}

function renderizarUsuarios(usuarios) {
    const tbody = document.getElementById("tablaUsuarios");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!usuarios || usuarios.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6">No hay usuarios para mostrar</td>
            </tr>
        `;
        return;
    }

    usuarios.forEach((usuario) => {
        const id = usuario.id_usuario ?? usuario.id ?? "";
        const nombre = usuario.nombre ?? "Sin nombre";
        const email = usuario.email ?? "Sin email";
        const rol = obtenerTextoRol(usuario);
        const estado = obtenerEstadoUsuario(usuario);
        const fechaCreado = formatearFecha(usuario.fecha_creacion);

        const fila = `
            <tr>
                <td>${escapeHtml(nombre)}</td>
                <td>${escapeHtml(email)}</td>
                <td>${escapeHtml(rol)}</td>
                <td>${escapeHtml(estado)}</td>
                <td>${escapeHtml(fechaCreado)}</td>
                <td>
                    <a href="#" class="view-order" data-id="${id}">
                        <span class="material-symbols-outlined">edit</span>
                    </a>
                </td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", fila);
    });

    tbody.querySelectorAll(".view-order").forEach((link) => {
        link.addEventListener("click", function (e) {
            e.preventDefault();

            const id = this.dataset.id;
            const usuario = usuariosCache.find(
                (u) => String(u.id_usuario ?? u.id ?? "") === String(id)
            );

            if (usuario) {
                abrirModalEditar(usuario);
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "No se encontró la información del usuario"
                });
            }
        });
    });
}

function abrirModalEditar(usuario) {
    document.getElementById("editIdUsuario").value = usuario.id_usuario ?? usuario.id ?? "";
    document.getElementById("editNombre").value = usuario.nombre || "";
    document.getElementById("editEmail").value = usuario.email || "";
    document.getElementById("editPassword").value = "";

    const estadoSelect = document.getElementById("estado");
    if (estadoSelect) {
        const estadoBool = usuario.estado === true || usuario.activo === true;
        estadoSelect.value = estadoBool ? "activo" : "inactivo";
    }

    document.getElementById("editUserModal").style.display = "flex";
}

function obtenerRolDesdeSelect() {
    const rolSelect = document.getElementById("rol");
    return rolSelect?.value || "ROLE_EMPLEADO";
}

function obtenerEstadoBooleanoDesdeSelect() {
    const estadoSelect = document.getElementById("estado");
    const valor = estadoSelect?.value;

    return valor === "activo";
}

function configurarFormularioCrear() {
    const form = document.getElementById("userForm");
    if (!form) return;

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const payload = {
            nombre: document.getElementById("nombre").value.trim(),
            email: document.getElementById("email").value.trim(),
            password: document.getElementById("password").value.trim(),
            rol: obtenerRolDesdeSelect()
        };

        console.log("PAYLOAD CREAR USUARIO:", payload);

        authFetch("/admin/empleado", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(async (res) => {
                if (!res.ok) {
                    let mensaje = "No se pudo crear el usuario";
                    try {
                        const errorData = await res.json();
                        mensaje = errorData.message || mensaje;
                    } catch (_) {}
                    throw new Error(mensaje);
                }
                return res.json().catch(() => ({}));
            })
            .then(() => {
                document.getElementById("userModal").style.display = "none";
                form.reset();

                Swal.fire({
                    icon: "success",
                    title: "Usuario creado",
                    text: "Se guardó correctamente"
                });

                terminoBusqueda = "";
                const searchInput = document.getElementById("searchInput");
                if (searchInput) searchInput.value = "";

                cargarUsuarios(0);
                cargarTotalesResumen();
            })
            .catch((error) => {
                console.error(error);

                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message || "No se pudo crear el usuario"
                });
            });
    });
}

function configurarFormularioEditar() {
    const form = document.getElementById("editUserForm");
    if (!form) return;

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const payload = {
            id_usuario: document.getElementById("editIdUsuario").value,
            nombre: document.getElementById("editNombre").value.trim(),
            email: document.getElementById("editEmail").value.trim(),
            estado: obtenerEstadoBooleanoDesdeSelect()
        };

        const password = document.getElementById("editPassword").value.trim();
        if (password !== "") {
            payload.password = password;
        }

        console.log("PAYLOAD EDITAR USUARIO:", payload);

        authFetch("/usuario/editar", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(async (res) => {
                if (!res.ok) {
                    let mensaje = "No se pudo editar el usuario";
                    try {
                        const errorData = await res.json();
                        mensaje = errorData.message || mensaje;
                    } catch (_) {}
                    throw new Error(mensaje);
                }
                return res.json().catch(() => ({}));
            })
            .then(() => {
                document.getElementById("editUserModal").style.display = "none";

                Swal.fire({
                    icon: "success",
                    title: "Usuario actualizado",
                    text: "Los cambios se guardaron correctamente"
                });

                cargarUsuarios(paginaActual);
                cargarTotalesResumen();
            })
            .catch((error) => {
                console.error(error);

                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message || "No se pudo editar el usuario"
                });
            });
    });
}

function renderizarPaginacion(totalPages, currentPage) {
    const contenedor = document.getElementById("paginacion");
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
        prevBtn.addEventListener("click", () => cargarUsuarios(currentPage - 1));
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

        btn.addEventListener("click", () => cargarUsuarios(i));
        contenedor.appendChild(btn);
    }

    if (currentPage < totalPages - 1) {
        const nextBtn = document.createElement("button");
        nextBtn.type = "button";
        nextBtn.innerText = "→";
        nextBtn.classList.add("btn-pagina");
        nextBtn.addEventListener("click", () => cargarUsuarios(currentPage + 1));
        contenedor.appendChild(nextBtn);
    }
}

function formatearFecha(fecha) {
    if (!fecha) return "Fecha no disponible";

    if (/^\d{4}-\d{2}-\d{2}$/.test(fecha)) {
        const [year, month, day] = fecha.split("-");
        return `${day}/${month}/${year}`;
    }

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

function obtenerTextoRol(usuario) {
    if (typeof usuario.rol === "string") return usuario.rol;
    if (usuario.rol && typeof usuario.rol.nombre_rol === "string") return usuario.rol.nombre_rol;
    return "Sin rol";
}

function obtenerEstadoUsuario(usuario) {
    if (typeof usuario.estado === "boolean") {
        return usuario.estado ? "Activo" : "Inactivo";
    }

    if (typeof usuario.estado === "string") {
        return usuario.estado;
    }

    if (typeof usuario.activo === "boolean") {
        return usuario.activo ? "Activo" : "Inactivo";
    }

    return "Sin estado";
}

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}