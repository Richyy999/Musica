package es.rbp.musica.modelo.entidad;

import java.util.List;

public class Carpeta implements Comparable<Carpeta> {

    private String ruta;
    private List<Cancion> canciones;

    public Carpeta(String ruta, List<Cancion> canciones) {
        this.ruta = ruta;
        this.canciones = canciones;
    }

    public String getNombre() {
        return ruta.substring(ruta.lastIndexOf("/") + 1);
    }

    public String getRuta() {
        return ruta;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    @Override
    public int compareTo(Carpeta o) {
        return getNombre().toLowerCase().compareTo(o.getNombre().toLowerCase());
    }

    @Override
    public String toString() {
        return getNombre().toLowerCase();
    }
}
