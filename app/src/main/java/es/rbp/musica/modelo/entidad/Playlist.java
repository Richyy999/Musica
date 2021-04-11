package es.rbp.musica.modelo.entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Playlist implements Serializable, Comparable<Playlist> {

    public static final long serialVersionUID = 3L;

    public static final String EXTRA_PLAYLIST = "indicePlaylis";

    public static final int INDICE_POR_DEFECTO = 0;

    private List<String> canciones;

    private String nombre;
    private String nombreFichero;

    public Playlist() {
    }

    public Playlist(String nombre, String nombreFichero) {
        this.nombre = nombre;
        this.nombreFichero = nombreFichero;
        this.canciones = new LinkedList<>();
    }

    public void eliminarCancion(int indice) {
        canciones.remove(indice);
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

    public String getNombreFichero() {
        return nombreFichero;
    }

    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    @Override
    public int compareTo(Playlist o) {
        return getNombreFichero().compareTo(o.getNombreFichero());
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "nombre='" + nombre + '\'' +
                ", nombreFichero='" + nombreFichero + '\'' +
                '}';
    }
}
