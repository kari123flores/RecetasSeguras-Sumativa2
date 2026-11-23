
document.addEventListener("DOMContentLoaded", () => {
    const whatsapp = document.getElementById("compartir-whatsapp");
    const copiar = document.getElementById("copiar-enlace");
    const mensaje = document.getElementById("mensaje-compartir");

    if (!whatsapp || !copiar) return;

    const id = whatsapp.dataset.id;
    const enlace = `${window.location.origin}/recetas/detalle/${id}`;

    whatsapp.href =
        `https://wa.me/?text=${encodeURIComponent("Mira esta receta: " + enlace)}`;

    copiar.addEventListener("click", async () => {
        try {
            await navigator.clipboard.writeText(enlace);
            mensaje.textContent = "✅ Enlace copiado";
        } catch (error) {
            mensaje.textContent = "No se pudo copiar el enlace";
        }
    });
});
