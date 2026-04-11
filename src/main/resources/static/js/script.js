const menuBtn = document.getElementById("menu-btn");
const closeBtn = document.getElementById("close-btn");
const aside = document.querySelector("aside");
const themeToggler = document.querySelector(".theme-toggler");
const logo = document.querySelector(".img-logo");

menuBtn.addEventListener("click", () => {
    aside.classList.add("open");
});

closeBtn.addEventListener("click", () => {
    aside.classList.remove("open");
});

themeToggler.addEventListener("click", () => {
    document.body.classList.toggle("dark-theme-variables");
    themeToggler.querySelector("span:nth-child(1)").classList.toggle("active");
    themeToggler.querySelector("span:nth-child(2)").classList.toggle("active");
    if (logo.style.filter === "invert(1)") {
        logo.style.filter = "none";
    } else {
        logo.style.filter = "invert(1)";
    }
});

// Abrir modal
document.getElementById("openModal").onclick = function () {
    document.getElementById("productModal").style.display = "flex"; // aquí sí flex
};

// Cerrar modal
document.getElementById("closeModal").onclick = function () {
    document.getElementById("productModal").style.display = "none";
};

// Cerrar al hacer clic fuera del contenido
window.onclick = function (event) {
    if (event.target == document.getElementById("productModal")) {
        document.getElementById("productModal").style.display = "none";
    }
};

// Selecciona todos los enlaces de Editar dentro de la tabla
const editLinks = document.querySelectorAll(".recent-orders a");

// Recorre cada enlace y agrega evento
editLinks.forEach((link) => {
    link.addEventListener("click", function (e) {
        e.preventDefault();

        // Encuentra la fila de la tabla donde se hizo clic
        const row = this.closest("tr");

        // Obtiene los valores de las celdas
        const id = row.querySelector("td:nth-child(4)").textContent; // SKU como ID
        const nombre = row.querySelector("td:nth-child(2)").textContent;
        const descripcion = row.querySelector("td:nth-child(2)").textContent; // ejemplo, podrías tener otra fuente
        const precio = row
            .querySelector("td:nth-child(7)")
            .textContent.replace("$", "");
        const stock = row.querySelector("td:nth-child(6)").textContent;
        const categoria = row.querySelector("td:nth-child(5)").textContent;
        const proveedor = row.querySelector("td:nth-child(3)").textContent;

        // Rellena los campos del modal
        document.getElementById("idProducto").value = id;
        document.getElementById("nombreEdit").value = nombre;
        document.getElementById("descripcionEdit").value = descripcion;
        document.getElementById("precioEdit").value = precio;
        document.getElementById("stockEdit").value = stock;
        document.getElementById("stockMinimoEdit").value = ""; // si no está en la tabla, lo dejas vacío
        document.getElementById("categoriaEdit").value = categoria;
        document.getElementById("proveedorEdit").value = proveedor;

        // Abre el modal
        document.getElementById("editProductModal").style.display = "flex";
    });
});

// Cerrar modal con la X
document.getElementById("closeEditModal").onclick = function () {
    document.getElementById("editProductModal").style.display = "none";
};

// Cerrar al hacer clic fuera del contenido
window.onclick = function (event) {
    if (event.target == document.getElementById("editProductModal")) {
        document.getElementById("editProductModal").style.display = "none";
    }
};


