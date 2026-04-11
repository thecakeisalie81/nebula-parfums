document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("providerModal");
    const closeModal = document.getElementById("closeProviderModal");

    // Abrir modal con datos de la fila
    document.querySelectorAll(".edit-provider").forEach((link) => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const row = this.closest("tr");

            document.getElementById("editProvNombre").value =
                row.cells[0].textContent;
            document.getElementById("editProvContacto").value =
                row.cells[1].textContent;
            document.getElementById("editProvEmail").value = row.cells[2].textContent;
            document.getElementById("editProvTelefono").value =
                row.cells[3].textContent;

            modal.style.display = "flex";
        });
    });

    // Cerrar modal
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});
