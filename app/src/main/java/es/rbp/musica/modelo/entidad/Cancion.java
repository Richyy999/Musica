package es.rbp.musica.modelo.entidad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.Serializable;

import es.rbp.musica.R;

public class Cancion implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String ALBUM_DESCONOCIDO = "Álbum desconocido";
    public static final String ARTISTA_DESCONOCIDO = "Artista desconocido";

    private String album;
    private String nombre;
    private String artista;
    private String datos;

    private int duracion;
    private int tamano;

    public Cancion(String album, String nombre, String artista, String datos, String duracion, String tamano) {
        this.album = album;
        this.nombre = nombre;
        this.artista = artista;
        this.datos = datos;
        this.duracion = Integer.parseInt(duracion);
        this.tamano = Integer.parseInt(tamano);
    }

    public Bitmap getImagenAlbum(Context context) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(datos);
        byte[] byts = retriever.getEmbeddedPicture();
        if (byts != null)
            return BitmapFactory.decodeByteArray(byts, 0, byts.length);
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.imagen_playlist);
    }

    public String getCarpetaPadre() {
        File file = new File(datos);
        return file.getParent();
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "nombre='" + nombre + '\'' +
                ", datos='" + datos + '\'' +
                '}';
    }
}
