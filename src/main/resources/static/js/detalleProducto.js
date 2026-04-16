const API_DETALLE_PRODUCTO = "/producto/buscar";
const API_CARRITO_MIO = "/carrito/mio";
const API_CARRITO_DETALLE_CREAR = "/carritodetalle/crear";
const URL_IMAGENES = "/uploads/";
const IMAGEN_DEFAULT = "/images/default.png";

let productoActual = null;

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
    cargarDetalleProducto();
    configurarBotonAgregarCarrito();
});

function cargarDetalleProducto() {
    const idProducto = localStorage.getItem("idProductoSeleccionado");

    if (!idProducto) {
        console.error("No hay producto seleccionado");
        window.location.href = "shop.html";
        return;
    }

    authFetch(`${API_DETALLE_PRODUCTO}?id=${encodeURIComponent(idProducto)}`)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Error al cargar producto: " + res.status);
            }

            return res.json();
        })
        .then((producto) => {
            productoActual = producto;
            pintarDetalleProducto(producto);
        })
        .catch((error) => {
            console.error("Error cargando detalle:", error);

            const nombre = document.getElementById("nombreProductoDetalle");
            const descripcion = document.getElementById("descripcionProductoDetalle");
            const precio = document.getElementById("precioProductoDetalle");
            const categoria = document.getElementById("categoriaProductoDetalle");
            const imagen = document.getElementById("imgProductoDetalle");

            if (nombre) {
                nombre.textContent = "Error al cargar producto";
            }

            if (descripcion) {
                descripcion.textContent = "No se pudo cargar la información del producto.";
            }

            if (precio) {
                precio.textContent = "₡0.00";
            }

            if (categoria) {
                categoria.textContent = "";
            }

            if (imagen) {
                imagen.src = IMAGEN_DEFAULT;
            }
        });
}

function pintarDetalleProducto(producto) {
    const img = document.getElementById("imgProductoDetalle");
    const nombre = document.getElementById("nombreProductoDetalle");
    const precio = document.getElementById("precioProductoDetalle");
    const descripcion = document.getElementById("descripcionProductoDetalle");
    const categoria = document.getElementById("categoriaProductoDetalle");

    const nombreImagen = producto.imagen
        ? encodeURIComponent(producto.imagen.trim())
        : "";

    const srcImagen = nombreImagen
        ? `${URL_IMAGENES}${nombreImagen}?t=${Date.now()}`
        : IMAGEN_DEFAULT;

    if (img) {
        img.src = srcImagen;
        img.alt = producto.nombre || "Producto";
        img.onerror = function () {
            this.onerror = null;
            this.src = IMAGEN_DEFAULT;
        };
    }

    if (nombre) {
        nombre.textContent = producto.nombre || "Producto sin nombre";
    }

    if (precio) {
        const precioFormateado = Number(producto.precio || 0).toLocaleString("es-CR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });

        precio.textContent = `₡${precioFormateado}`;
    }

    if (descripcion) {
        descripcion.textContent =
            producto.descripcion || "Este producto no tiene descripción.";
    }

    if (categoria) {
        categoria.textContent = producto.categoria
            ? `Categoría: ${producto.categoria.nombre}`
            : "Sin categoría";
    }
}

function configurarBotonAgregarCarrito() {
    const btnAgregar = document.getElementById("btnAgregarCarritoDetalle");

    if (!btnAgregar) return;

    btnAgregar.addEventListener("click", function () {
        if (!productoActual) {
            console.error("Producto no cargado todavía");
            return;
        }

        agregarAlCarrito(productoActual.id_producto, productoActual.precio);
    });
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
            const lista = Array.isArray(carrito.listaCarritoDetalles)
                ? carrito.listaCarritoDetalles
                : [];

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