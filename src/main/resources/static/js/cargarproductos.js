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

    response = await authFetch("/producto/constock");
    data = await response.text();
    const enStock = Number(data.trim());
    document.querySelector("#enStock").innerHTML = enStock;

    response = await authFetch("/producto/nostock");
    data = await response.text();
    const noStock = Number(data.trim());
    document.querySelector("#sinStock").innerHTML = noStock;

    response = await authFetch("/producto/lowstock");
    data = await response.text();
    const lowStock = Number(data.trim());
    document.querySelector("#bajoStock").innerHTML = lowStock;
}





document.addEventListener("DOMContentLoaded", () => {
    cargarCuadros();
    cargarUltimosMovimientos()

    const searchInput = document.getElementById("searchInput");

    if (searchInput) {
        searchInput.addEventListener("input", function () {
            clearTimeout(debounceTimer);

            debounceTimer = setTimeout(() => {
                const valor = this.value.trim();

                if (valor.length === 0 || valor.length >= 2) {
                    terminoBusqueda = valor;
                    cargarProductos(0);
                }
            }, 400);
        });
    }

    document.getElementById("openModal").onclick = function () {
        document.getElementById("productModal").style.display = "flex"; // aquí sí flex
    };

    const token = obtenerToken();
    cargarCategorias("categoria");
    cargarProveedores("proveedor");

    if (!token) {
        window.location.replace("/login.html");
    }

    cargarProductos();



    const inputImagenEdit = document.getElementById("imagenEdit");


    if (inputImagenEdit) {
        inputImagenEdit.addEventListener("change", function () {
            const file = this.files[0];
            if (file) {
                document.getElementById("previewEdit").src = URL.createObjectURL(file);
            }
        });
    }

});


function authFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            "Authorization": "Bearer " + token
        }
    });
}


let paginaActual = 0;
let totalPaginas = 0;
let terminoBusqueda = "";
let debounceTimer = null;

function cargarProductos(page = 0) {
    let url = "";

    if (terminoBusqueda.trim() === "") {
        url = `/producto/traer?page=${page}&size=10`;
    } else {
        url = `/producto/resultados?nombre=${encodeURIComponent(terminoBusqueda.trim())}&page=${page}&size=10`;
    }

    console.log("URL USADA:", url);
    console.log("terminoBusqueda:", "[" + terminoBusqueda + "]");

    authFetch(url)
        .then(res => {
            if (!res.ok) {
                throw new Error("Error en la petición: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            console.log("RESPUESTA:", data);

            renderizarProductos(data.content);

            const currentPage = data.number ?? data.page?.number ?? 0;
            const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

            console.log("currentPage:", currentPage);
            console.log("totalPages:", totalPages);

            paginaActual = currentPage;
            totalPaginas = totalPages;

            renderizarPaginacion(totalPages, currentPage);
        })
        .catch(err => {
            console.error("Error cargando productos:", err);
        });
}


function renderizarProductos(productos) {
    const tbody = document.getElementById("tablaProductos");
    const cacheBust = Date.now();

    tbody.innerHTML = "";

    productos.forEach(producto => {
        const nombreImagen = producto.imagen
            ? encodeURIComponent(producto.imagen.trim())
            : "";

        const srcImagen = nombreImagen
            ? `/uploads/${nombreImagen}?t=${cacheBust}`
            : `/images/default.png`;

        const fila = `
            <tr>
                <td>
                    <img 
                        src="${srcImagen}" 
                        width="50"
                        onerror="this.onerror=null; this.src='/images/default.png';"
                    >
                </td>
                <td>${producto.nombre}</td>
                <td>₡${producto.precio}</td>
                <td>${producto.descripcion}</td>
                <td>${producto.stock_actual}</td>
                <td>${producto.stock_minimo}</td>
                <td>${producto.categoria ? producto.categoria.nombre : "Sin categoría"}</td>
                <td>${producto.proveedor ? producto.proveedor.nombre : "Sin proveedor"}</td>
                <td>
                    <a href="javascript:void(0)" onclick="abrirModalEditar(${producto.id_producto})">Editar</a>
                </td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", fila);
    });
}

function abrirModalEditar(id) {

    Promise.all([
        cargarCategorias("categoriaEdit"),
        cargarProveedores("proveedorEdit")
    ]).then(() => {
        authFetch(`/producto/buscar?id=${id}`)
            .then(res => res.json())
            .then(producto => {

                document.getElementById("idProducto").value = producto.id_producto;
                document.getElementById("nombreEdit").value = producto.nombre;
                document.getElementById("descripcionEdit").value = producto.descripcion;
                document.getElementById("precioEdit").value = producto.precio;
                document.getElementById("stockEdit").value = producto.stock_actual;
                document.getElementById("stockMinimoEdit").value = producto.stock_minimo;

                document.getElementById("categoriaEdit").value = producto.categoria.id_categoria;
                document.getElementById("proveedorEdit").value = producto.proveedor.id_proveedor;

                document.getElementById("previewEdit").src =
                    `/uploads/${producto.imagen || 'default.png'}?t=${Date.now()}`;

                document.getElementById("editProductModal").style.display = "flex";
            });

    });
}

function renderizarPaginacion(totalPages, currentPage) {

    const contenedor = document.getElementById("paginacion");
    contenedor.innerHTML = "";

    for (let i = 0; i < totalPages; i++) {

        const btn = document.createElement("button");
        btn.innerText = i + 1;

        btn.classList.add("btn-pagina");

        if (i === currentPage) {
            btn.classList.add("active");
        }

        btn.addEventListener("click", () => {
            cargarProductos(i);
        });

        contenedor.appendChild(btn);
    }
}

document.getElementById("closeModal").onclick = function () {
    document.getElementById("productModal").style.display = "none";
};


window.onclick = function (event) {

    if (event.target == document.getElementById("productModal")) {
        document.getElementById("productModal").style.display = "none";
    }

    if (event.target == document.getElementById("editProductModal")) {
        document.getElementById("editProductModal").style.display = "none";
    }
};


document.getElementById("editProductForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const token = obtenerToken();
    const formData = new FormData();

    formData.append("id_producto", document.getElementById("idProducto").value);
    formData.append("nombre", document.getElementById("nombreEdit").value);
    formData.append("descripcion", document.getElementById("descripcionEdit").value);
    formData.append("precio", document.getElementById("precioEdit").value);
    formData.append("stock_actual", document.getElementById("stockEdit").value);
    formData.append("stock_minimo", document.getElementById("stockMinimoEdit").value);
    formData.append("categoria", document.getElementById("categoriaEdit").value);
    formData.append("proveedor", document.getElementById("proveedorEdit").value);

    const imagen = document.getElementById("imagenEdit").files[0];
    if (imagen) {
        formData.append("imagen", imagen);
    }

    fetch("/producto/editar", {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token
        },
        body: formData
    })
        .then(res => {
            if (!res.ok) throw new Error("Error al editar");
            return res.json();
        })
        .then(() => {

            document.getElementById("editProductModal").style.display = "none";

            Swal.fire({
                icon: "success",
                title: "Producto actualizado"
            });

            cargarProductos(paginaActual);
        })
        .catch(err => {
            console.error(err);
            Swal.fire({
                icon: "error",
                title: "Error al editar"
            });
        });
});

// Cerrar modal con la X
document.getElementById("closeEditModal").onclick = function () {
    document.getElementById("editProductModal").style.display = "none";
};



document.getElementById("productForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const token = obtenerToken();

    const formData = new FormData();

    // 📦 datos del producto
    formData.append("nombre", document.getElementById("nombre").value);
    formData.append("descripcion", document.getElementById("descripcion").value);
    formData.append("precio", document.getElementById("precio").value);
    formData.append("stock_actual", document.getElementById("stock").value);
    formData.append("stock_minimo", document.getElementById("stockMinimo").value);
    formData.append("categoria", document.getElementById("categoria").value);
    formData.append("proveedor", document.getElementById("proveedor").value);

    // 🖼️ imagen
    const imagen = document.getElementById("imagen").files[0];
    if (imagen) {
        formData.append("imagen", imagen);
    }

    fetch("/producto/crear", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        },
        body: formData
    })
        .then(res => {
            if (!res.ok) throw new Error("Error al crear producto");
            return res.json();
        })
        .then(() => {

            document.getElementById("productModal").style.display = "none";
            document.getElementById("productForm").reset();
            cargarProductos(paginaActual);

            Swal.fire({
                icon: "success",
                title: "Producto creado",
                text: "Se guardó correctamente"
            });
        })
        .catch(err => {
            console.error(err);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo crear el producto"
            });
        });
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

function cargarCategorias(elemento) {
    const token = obtenerToken();

    return fetch("/categoria/traer", {   // 👈 CLAVE
        headers: { "Authorization": "Bearer " + token }
    })
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById(elemento);
            select.innerHTML = "<option value=''>Seleccione</option>";

            data.forEach(c => {
                const option = document.createElement("option");
                option.value = c.id_categoria;
                option.textContent = c.nombre;
                select.appendChild(option);
            });
        });
}

function cargarProveedores(elemento){
    const token = obtenerToken();

    return fetch("/proveedor/traer", {   // 👈 AGREGA return
        headers: { "Authorization": "Bearer " + token }
    })
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById(elemento);
            select.innerHTML = "<option value=''>Seleccione</option>";

            data.forEach(p => {
                const option = document.createElement("option");
                option.value = p.id_proveedor;
                option.textContent = p.nombre;
                select.appendChild(option);
            });
        });
}


