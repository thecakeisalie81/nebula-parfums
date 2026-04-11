document.addEventListener("DOMContentLoaded", () => {
    const viewLinks = document.querySelectorAll(".view-order");
    const modal = document.getElementById("orderModal");
    const closeModal = document.getElementById("closeOrderModal");

    viewLinks.forEach((link) => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const row = this.closest("tr");

            // Rellenar datos
            document.getElementById("pedidoId").value = row.cells[0].textContent;
            document.getElementById("pedidoCliente").value = row.cells[1].textContent;
            document.getElementById("pedidoFecha").value = row.cells[2].textContent;
            document.getElementById("pedidoProductos").value =
                row.cells[3].textContent;
            document.getElementById("pedidoTotal").value = row.cells[4].textContent;
            document.getElementById("pedidoEstado").value = row.cells[5].textContent;

            modal.style.display = "flex";
        });
    });

    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});
