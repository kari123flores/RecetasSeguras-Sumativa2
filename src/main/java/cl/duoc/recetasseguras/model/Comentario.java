
package cl.duoc.recetasseguras.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(nullable = false)
    private int valoracion;

    public Comentario() {}

    public Long getId() { return id; }
    public Receta getReceta() { return receta; }
    public String getUsuario() { return usuario; }
    public String getTexto() { return texto; }
    public int getValoracion() { return valoracion; }

    public void setReceta(Receta receta) { this.receta = receta; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setTexto(String texto) { this.texto = texto; }
    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }
}
