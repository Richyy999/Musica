package es.rbp.musica.modelo.entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {

    public static final long serialVersionUID = 3L;

    public static final String EXTRA_PLAYLIST = "indicePlaylis";

    public static final int INDICE_POR_DEFECTO = 0;

    private List<String> canciones;

    private String nombre;
    private String rutaImagen;

    public Playlist() {
    }

    public Playlist(String nombre) {
        this.nombre = nombre;
        this.canciones = new ArrayList<>();
    }

    public List<String> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<String> canciones) {
        this.canciones = canciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}
