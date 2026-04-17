const API_PRODUCTOS = "/producto/traer";
const API_CARRITO_DETALLE_CREAR = "/carritodetalle/crear";
const API_CARRITO_MIO = "/carrito/mio";
const URL_IMAGENES = "/uploads/";
const IMAGEN_DEFAULT = "/images/default.png";

let paginaActual = 0;
let totalPaginas = 0;

const productosPorPagina = 12;

let nombreFiltro = "";
let idCategoriaFiltro = "";
let precioMinimoFiltro = null;
let precioMaximoFiltro = null;
let debounceBusqueda = null;

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

    actualizarBadgeCarrito();

    const formBuscar = document.getElementById("formBuscar");
    const inputNombre = document.getElementById("inputNombre");

    if (formBuscar) {
        formBuscar.addEventListener("submit", function (e) {
            e.preventDefault();
        });
    }

    if (inputNombre) {
        inputNombre.addEventListener("input", function () {
            clearTimeout(debounceBusqueda);

            debounceBusqueda = setTimeout(() => {
                nombreFiltro = inputNombre.value.trim();
                cargarProductos(0);
            }, 400);
        });
    }

    document.querySelectorAll(".filtro-categoria").forEach((categoria) => {
        categoria.addEventListener("click", function (e) {
            e.preventDefault();

            document.querySelectorAll(".filtro-categoria").forEach((item) => {
                item.classList.remove("active");
            });

            this.classList.add("active");

            idCategoriaFiltro = this.dataset.id || "";
            cargarProductos(0);
        });
    });

    document.querySelectorAll("input[id^='price-']").forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            aplicarFiltroPrecio(this.id);
            cargarProductos(0);
        });
    });

    cargarProductos(0);
});

function construirUrlProductos(page = 0) {
    const params = new URLSearchParams();

    params.append("page", page);
    params.append("size", productosPorPagina);
    params.append("disponible", 0);

    if (nombreFiltro !== "") {
        params.append("nombre", nombreFiltro);
    }

    if (idCategoriaFiltro !== "") {
        params.append("idCategoria", idCategoriaFiltro);
    }

    if (precioMinimoFiltro !== null) {
        params.append("precioMinimo", precioMinimoFiltro);
    }

    if (precioMaximoFiltro !== null) {
        params.append("precioMaximo", precioMaximoFiltro);
    }

    return `${API_PRODUCTOS}?${params.toString()}`;
}

function cargarProductos(page = 0) {
    const url = construirUrlProductos(page);

    authFetch(url)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error en la petición: " + res.status);
            }

            return res.json();
        })
        .then((data) => {
            const productos = data.content || [];
            const currentPage = data.number ?? data.page?.number ?? 0;
            const totalPages = data.totalPages ?? data.page?.totalPages ?? 0;

            paginaActual = currentPage;
            totalPaginas = totalPages;

            renderizarProductos(productos);
            renderizarPaginacion(totalPages, currentPage);
        })
        .catch((error) => {
            console.error("Error cargando productos:", error);

            const contenedor = document.getElementById("contenedorProductos");

            if (contenedor) {
                contenedor.innerHTML = `
                    <div class="col-12 text-center">
                        <h5>Error al cargar productos</h5>
                    </div>
                `;
            }

            const paginacion = document.getElementById("paginacionProductos");
            if (paginacion) {
                paginacion.innerHTML = "";
            }
        });
}

function renderizarProductos(productos) {
    const contenedor = document.getElementById("contenedorProductos");

    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (!productos || productos.length === 0) {
        contenedor.innerHTML = `
            <div class="col-12 text-center">
                <h5>No hay productos para mostrar</h5>
            </div>
        `;
        return;
    }

    const cacheBust = Date.now();

    productos.forEach((producto) => {
        const nombreImagen = producto.imagen
            ? encodeURIComponent(producto.imagen.trim())
            : "";

        const srcImagen = nombreImagen
            ? `${URL_IMAGENES}${nombreImagen}?t=${cacheBust}`
            : IMAGEN_DEFAULT;

        const nombreCategoria = producto.categoria
            ? producto.categoria.nombre
            : "Sin categoría";

        const precioSeguro = Number(producto.precio || 0);

        const card = `
            <div class="col-lg-3 col-md-6 col-sm-12 pb-1">
                <div class="card product-item border-0 mb-4">

                    <div class="card-header product-img position-relative overflow-hidden bg-transparent border p-0">
                        <img
                            class="img-fluid w-100"
                            src="${srcImagen}"
                            alt="${producto.nombre}"
                            onerror="this.onerror=null; this.src='${IMAGEN_DEFAULT}';"
                        >
                    </div>

                    <div class="card-body border-left border-right text-center p-0 pt-4 pb-3">
                        <h6 class="text-truncate mb-3">${producto.nombre}</h6>
                        <small class="text-muted d-block mb-2">${nombreCategoria}</small>

                        <div class="d-flex justify-content-center">
                            <h6>₡${precioSeguro.toFixed(2)}</h6>
                        </div>
                    </div>

                    <div class="card-footer d-flex justify-content-between bg-light border">
                        <a
                            href="javascript:void(0)"
                            class="btn btn-sm text-dark p-0"
                            onclick="verDetalleProducto(${producto.id_producto})"
                        >
                            <i class="fas fa-eye text-primary mr-1"></i>Ver detalles
                        </a>

                        <a
                            href="javascript:void(0)"
                            class="btn btn-sm text-dark p-0"
                            onclick="agregarAlCarrito(${producto.id_producto}, ${precioSeguro})"
                        >
                            <i class="fas fa-shopping-cart text-primary mr-1"></i>Añadir
                        </a>
                    </div>

                </div>
            </div>
        `;

        contenedor.insertAdjacentHTML("beforeend", card);
    });
}

function renderizarPaginacion(totalPages, currentPage) {
    const contenedor = document.getElementById("paginacionProductos");

    if (!contenedor) return;

    contenedor.innerHTML = "";

    if (totalPages <= 1) return;

    if (currentPage > 0) {
        contenedor.insertAdjacentHTML(
            "beforeend",
            `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="cargarProductos(${currentPage - 1})">
                    &laquo;
                </a>
            </li>
            `
        );
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
        contenedor.insertAdjacentHTML(
            "beforeend",
            `
            <li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="javascript:void(0)" onclick="cargarProductos(${i})">
                    ${i + 1}
                </a>
            </li>
            `
        );
    }

    if (currentPage < totalPages - 1) {
        contenedor.insertAdjacentHTML(
            "beforeend",
            `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="cargarProductos(${currentPage + 1})">
                    &raquo;
                </a>
            </li>
            `
        );
    }
}

function aplicarFiltroPrecio(idSeleccionado) {
    document.querySelectorAll("input[id^='price-']").forEach((input) => {
        input.checked = input.id === idSeleccionado;
    });

    precioMinimoFiltro = null;
    precioMaximoFiltro = null;

    if (idSeleccionado === "price-1") {
        precioMinimoFiltro = 0;
        precioMaximoFiltro = 30000;
    }

    if (idSeleccionado === "price-2") {
        precioMinimoFiltro = 30000;
        precioMaximoFiltro = 80000;
    }

    if (idSeleccionado === "price-3") {
        precioMinimoFiltro = 80000;
        precioMaximoFiltro = 150000;
    }

    if (idSeleccionado === "price-4") {
        precioMinimoFiltro = 150000;
        precioMaximoFiltro = 500000;
    }

    if (idSeleccionado === "price-all") {
        precioMinimoFiltro = null;
        precioMaximoFiltro = null;
    }
}

function verDetalleProducto(idProducto) {
    localStorage.setItem("idProductoSeleccionado", idProducto);
    window.location.href = "detail.html";
}

function agregarAlCarrito(idProducto, precioProducto) {
    authFetch(API_CARRITO_MIO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener carrito: " + res.status);
            }
            return res.json();
        })
        .then((carrito) => {
            const payload = {
                cantidad: 1,
                precio: Number(precioProducto),
                id_carrito: carrito.id_carrito,
                id_producto: idProducto,
                id_carrito_detalle: null
            };

            return authFetch(API_CARRITO_DETALLE_CREAR, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });
        })
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al agregar al carrito: " + res.status);
            }
            return res.text();
        })
        .then((mensaje) => {
            return actualizarBadgeCarrito().then(() => {
                Swal.fire({
                    icon: "success",
                    title: "Producto agregado",
                    text: "El producto se añadió correctamente al carrito",
                    confirmButtonText: "Aceptar"
                });
            });
        })
        .catch((error) => {
            console.error("Error agregando al carrito:", error);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo agregar el producto al carrito",
                confirmButtonText: "Aceptar"
            });
        });
}

function actualizarBadgeCarrito() {
    return authFetch(API_CARRITO_MIO)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al obtener carrito: " + res.status);
            }
            return res.json();
        })
        .then((carrito) => {
            const lista = carrito.listaCarritoDetalles || [];

            const totalDetalles = lista.length;

            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = totalDetalles;
            });
        })
        .catch((error) => {
            console.error("Error actualizando badge carrito:", error);

            document.querySelectorAll(".badge-carrito").forEach((badge) => {
                badge.textContent = "0";
            });
        });
}