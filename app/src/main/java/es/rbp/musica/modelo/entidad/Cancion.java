package es.rbp.musica.modelo.entidad;

import java.io.Serializable;

public class Cancion implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String ALBUM_DESCONOCIDO = "Álbum desconocido";
    public static final String ARTISTA_DESCONOCIDO = "Artista desconocido";

    private String nombreAlbum;
    private String imagenAlbum;
    private String nombre;
    private String artista;
    private String datos;
    private String ruta;

    public Cancion(String nombreAlbum, String imagenAlbum, String nombre, String artista, String datos, String ruta) {
        this.nombreAlbum = nombreAlbum;
        this.imagenAlbum = imagenAlbum;
        this.nombre = nombre;
        this.artista = artista;
        this.datos = datos;
        this.ruta = ruta;
    }

    public String getCarpetaPadre() {
        return ruta.substring(0, ruta.lastIndexOf("/"));
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }

    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
    }

    public String getImagenAlbum() {
        return imagenAlbum;
    }

    public void setImagenAlbum(String imagenAlbum) {
        this.imagenAlbum = imagenAlbum;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
