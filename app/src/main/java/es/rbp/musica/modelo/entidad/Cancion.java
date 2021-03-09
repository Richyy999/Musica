package es.rbp.musica.modelo.entidad;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.Serializable;

public class Cancion extends File implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String ALBUM_DESCONOCIDO = "Álbum desconocido";
    public static final String ARTISTA_DESCONOCIDO = "Artista desconocido";

    private String album;
    private String nombre;
    private String artista;

    public Cancion(@NonNull String pathname, String album, String nombre, String artista) {
        super(pathname);
        this.album = album;
        this.nombre = nombre;
        this.artista = artista;
    }
}
