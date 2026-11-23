
package cl.duoc.recetasseguras.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recetas")
public class Receta {

    @Id
    private Integer id;

    private String nombre;
    private String tipo;
    private String ingrediente;
    private String pais;
    private String dificultad;
    private int tiempo;

    @Column(length = 3000)
    private String instrucciones;

    private String imagen;

    // Video de la receta
    private String video;

    public Receta() {}

    public Receta(int id, String nombre, String tipo,
                  String ingrediente, String pais,
                  String dificultad, int tiempo,
                  String instrucciones, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ingrediente = ingrediente;
        this.pais = pais;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
        this.instrucciones = instrucciones;
        this.imagen = imagen;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getIngrediente() { return ingrediente; }
    public String getPais() { return pais; }
    public String getDificultad() { return dificultad; }
    public int getTiempo() { return tiempo; }
    public String getInstrucciones() { return instrucciones; }
    public String getImagen() { return imagen; }
    public String getVideo() { return video; }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
