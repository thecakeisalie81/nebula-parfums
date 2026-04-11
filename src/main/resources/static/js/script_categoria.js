document.addEventListener("DOMContentLoaded", () => {
    const fileInput = document.getElementById("imagenCategoria");
    const fileName = document.getElementById("fileName");
    const previewImage = document.getElementById("previewImage");

    if (fileInput) {
        fileInput.addEventListener("change", function () {
            const file = this.files[0];
            if (file) {
                fileName.textContent = file.name; // muestra el nombre del archivo
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewImage.style.display = "block"; // muestra la vista previa
                };
                reader.readAsDataURL(file);
            } else {
                fileName.textContent = "Ningún archivo seleccionado";
                previewImage.src = "";
                previewImage.style.display = "none";
            }
        });
    }
});

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
