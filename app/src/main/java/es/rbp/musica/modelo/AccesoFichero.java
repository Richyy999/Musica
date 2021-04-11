package es.rbp.musica.modelo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Playlist;

import java.nio.file.Files;
import java.util.Properties;

import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_MODO_OSCURO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_NUMERO_PLAYLISTS;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_ULTIMO_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_ULTIMO_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO;

public class AccesoFichero {

    public static final int REQUEST_PERMISO_LECTURA = 1;

    private static final String CARPETA_PLAYLISTS = "playlists/";

    private static final String CARPETA_IMAGENES_PLAYLIST = CARPETA_PLAYLISTS + "img/";

    private static final String RUTA_FICHERO_AJUSTES = "ajustes.properties";
    private static final String RUTA_CARPETAS_OCULTAS = "carpetasOcultas.txt";
    private static final String RUTA_FAVORITOS = "favoritos.txt";

    private static final String EXTENSION_PLAYLISTS = ".obj";

    private static final String TAG = "ACCESO FICHERO";

    private static AccesoFichero accesoFichero;

    private List<Playlist> playlists;

    private List<Cancion> todasCanciones;

    private List<String> favoritos;

    private Context context;

    private AccesoFichero(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AccesoFichero getInstance(Context context) {
        if (accesoFichero == null)
            accesoFichero = new AccesoFichero(context);
        return accesoFichero;
    }

    /**
     * Lee los ficheros que contienen la información de los ajustes y crean una instancia de la clase con los datos de los ficheros.
     *
     * @return Instancia de {@link Ajustes}, null si hay algún fallo
     */
    public Ajustes leerAjustes() {
        File ficheroProperties = new File(context.getFilesDir(), RUTA_FICHERO_AJUSTES);
        if (!ficheroProperties.exists()) {
            Log.d(TAG, "Ajustes properties no existe");
            return null;
        }
        File ficheroCarpetas = new File(context.getFilesDir(), RUTA_CARPETAS_OCULTAS);
        if (!ficheroCarpetas.exists()) {
            Log.d(TAG, "Ajustes carpetas ocultas no existe");
            return null;
        }

        Properties properties = new Properties();
        List<String> carpetasOcultas;
        try {
            properties.load(new FileInputStream(ficheroProperties));
            carpetasOcultas = Files.readAllLines(ficheroCarpetas.toPath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        if (!properties.containsKey(PROPIEDAD_MODO_OSCURO) || !properties.containsKey(PROPIEDAD_FILTRO_TAMANO)
                || !properties.containsKey(PROPIEDAD_FILTRO_DURACION) || !properties.containsKey(PROPIEDAD_ULTIMO_FILTRO_TAMANO)
                || !properties.containsKey(PROPIEDAD_ULTIMO_FILTRO_DURACION) || !properties.containsKey(PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO)
                || !properties.containsKey(PROPIEDAD_NUMERO_PLAYLISTS)) {
            Log.d(TAG, "Ajustes properties corrupto");
            return null;
        }
        boolean modoOscuro = Boolean.parseBoolean(properties.getProperty(PROPIEDAD_MODO_OSCURO));
        boolean utilizarNombreDeArchivo = Boolean.parseBoolean(properties.getProperty(PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO));
        int filtroTamano = Integer.parseInt(properties.getProperty(PROPIEDAD_FILTRO_TAMANO));
        int filtroDuracion = Integer.parseInt(properties.getProperty(PROPIEDAD_FILTRO_DURACION));
        int ultimoFiltroTamano = Integer.parseInt(properties.getProperty(PROPIEDAD_ULTIMO_FILTRO_TAMANO));
        int ultimoFiltroDuracion = Integer.parseInt(properties.getProperty(PROPIEDAD_ULTIMO_FILTRO_DURACION));
        int numPlaylists = Integer.parseInt(properties.getProperty(PROPIEDAD_NUMERO_PLAYLISTS));

        return new Ajustes(carpetasOcultas, modoOscuro, utilizarNombreDeArchivo, filtroTamano, filtroDuracion, ultimoFiltroTamano, ultimoFiltroDuracion, numPlaylists);
    }

    /**
     * Almacena los valores de los ajustes en los ficheros
     *
     * @param ajustes instancia de la clase con los datos de los ajustes
     * @throws IOException error al guardar los datos
     */
    public void guardarAjustes(Ajustes ajustes) throws IOException {
        String modoOscuro = String.valueOf(ajustes.isModoOscuro());
        String utilizarNombreDelArchivo = String.valueOf(ajustes.isUtilizarNombreDeArchivo());
        String filtroTamano = String.valueOf(ajustes.getFiltroTanamoActual());
        String filtroDuracion = String.valueOf(ajustes.getFiltroDuracionActual());
        String ultimoFiltroTamano = String.valueOf(ajustes.getUltimoFiltroTamano());
        String ultimoFiltroDuracion = String.valueOf(ajustes.getUltimoFiltroDuracion());
        String numPlaylists = String.valueOf(ajustes.getNumPlaylists());

        File ficheroProperties = new File(context.getFilesDir(), RUTA_FICHERO_AJUSTES);
        if (!ficheroProperties.exists())
            ficheroProperties.createNewFile();

        Properties properties = new Properties();
        properties.load(new FileInputStream(ficheroProperties));
        properties.setProperty(PROPIEDAD_MODO_OSCURO, modoOscuro);
        properties.setProperty(PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO, utilizarNombreDelArchivo);
        properties.setProperty(PROPIEDAD_FILTRO_TAMANO, filtroTamano);
        properties.setProperty(PROPIEDAD_FILTRO_DURACION, filtroDuracion);
        properties.setProperty(PROPIEDAD_ULTIMO_FILTRO_TAMANO, ultimoFiltroTamano);
        properties.setProperty(PROPIEDAD_ULTIMO_FILTRO_DURACION, ultimoFiltroDuracion);
        properties.setProperty(PROPIEDAD_NUMERO_PLAYLISTS, numPlaylists);
        properties.store(new FileOutputStream(ficheroProperties), "");

        File ficheroCarpetas = new File(context.getFilesDir(), RUTA_CARPETAS_OCULTAS);
        StringBuilder carpetasOcultasBuilder = new StringBuilder();
        for (String carpeta : ajustes.getCarpetasOcultas()) {
            carpetasOcultasBuilder.append(carpeta).append("\n");
        }
        String carpetasOcultas = carpetasOcultasBuilder.toString();
        Log.d(TAG, "CARPETAS OCULTAS:\n" + carpetasOcultas);

        FileOutputStream fos = new FileOutputStream(ficheroCarpetas);
        fos.write(carpetasOcultas.getBytes());
        fos.close();
        Log.i(TAG, "Ajustes guardados");
    }

    /**
     * Devuelve todas las canciones del dispositivo. Si no las ha cargado antes, las lee
     *
     * @return todas las canciones del dispositivo sin filtrar
     */
    public List<Cancion> getTodasCanciones() {
        if (todasCanciones == null)
            leerCanciones();
        return todasCanciones;
    }

    /**
     * Lee las todas las canciones del dispositivo
     */
    private void leerCanciones() {
        String seleccion = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] proyeccion = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proyeccion, seleccion, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            todasCanciones = new ArrayList<>();
            do {
                if (new File(cursor.getString(3)).exists()) {
                    Cancion cancion = new Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4), cursor.getString(5));

                    if (cancion.getArtista().equals(Cancion.UNKNOWN))
                        cancion.setArtista(Cancion.ARTISTA_DESCONOCIDO);

                    if (cancion.getAlbum().equals(cancion.getCarpetaPadre().substring(cancion.getCarpetaPadre().lastIndexOf("/") + 1)))
                        cancion.setAlbum(Cancion.ALBUM_DESCONOCIDO);

                    todasCanciones.add(cancion);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else
            Log.i(TAG, "Sin música");
    }

    /**
     * Filtra las todas las canciones del dispositvo y devuelve las canciones que estén en el fichero de favoritos
     *
     * @return las canciones que están en el fichero de favoritos
     */
    public List<Cancion> getFavoritos() {
        if (this.favoritos == null)
            leerFavoritos();

        if (todasCanciones == null)
            leerCanciones();

        List<Cancion> favoritos = new ArrayList<>();
        for (Cancion cancion : todasCanciones) {
            if (this.favoritos.contains(cancion.getDatos()))
                favoritos.add(cancion);
        }

        return favoritos;
    }

    /**
     * Lee el fichero con los favoritos
     */
    private void leerFavoritos() {
        favoritos = new ArrayList<>();
        try {
            File archivo = new File(context.getFilesDir(), RUTA_FAVORITOS);
            if (!archivo.exists())
                archivo.createNewFile();

            favoritos = Files.readAllLines(archivo.toPath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void guardarFavoritos() {
        File ficheroFavoritos = new File(context.getFilesDir(), RUTA_FAVORITOS);
        StringBuilder contenido = new StringBuilder();
        for (String estring : favoritos) {
            contenido.append(estring).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(ficheroFavoritos)) {
            fos.write(contenido.toString().getBytes());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void anadirFavorito(String favorito) {
        favoritos.add(favorito);
        guardarFavoritos();
    }

    public void eliminarFavorito(String favorito) {
        favoritos.remove(favorito);
        guardarFavoritos();
    }

    public List<Playlist> getPlaylists() {
        if (playlists == null)
            leerPlaylists();

        return playlists;
    }

    public Playlist crearPlaylist(String nombrePlaylist, Ajustes ajustes) throws IOException {
        Playlist nuevaPlaylist = new Playlist(nombrePlaylist, ajustes.getNombreFicheroNuevaPLaylist());
        ajustes.setNumPlaylists(ajustes.getNumPlaylists() + 1);

        guardarAjustes(ajustes);

        return nuevaPlaylist;
    }

    private void leerPlaylists() {
        playlists = new ArrayList<>();
        File carpetaPlaylist = new File(context.getFilesDir(), CARPETA_PLAYLISTS);
        if (!carpetaPlaylist.exists()) {
            carpetaPlaylist.mkdir();
            Log.i(TAG, "Creada carpeta playlist");
        } else
            Log.i(TAG, "Carpeta playlist existe");

        Log.d(TAG, "Carpeta playlist es directorio: " + carpetaPlaylist.isDirectory());
        Log.i(TAG, "Número de playlists: " + carpetaPlaylist.length());

        File[] ficheros = carpetaPlaylist.listFiles();

        if (ficheros != null) {
            for (File fichero : ficheros) {
                if (!fichero.isDirectory()) {
                    Log.d(TAG, "Nombre fichero playlist: " + fichero.getAbsolutePath());
                    try (FileInputStream fis = new FileInputStream(fichero);
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        Playlist playlist = (Playlist) ois.readObject();
                        playlists.add(playlist);
                    } catch (IOException | ClassNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        } else
            Log.i(TAG, "Ficheros playlist = null");
    }

    public Playlist getPlaylistPorIndice(int indice) {
        if (playlists == null)
            leerPlaylists();

        return playlists.get(indice);
    }

    public void guardarPlaylist(Playlist playlist) {
        File carpetaPlaylist = new File(context.getFilesDir(), CARPETA_PLAYLISTS);
        File ficheroPlaylist = new File(carpetaPlaylist, playlist.getNombreFichero() + EXTENSION_PLAYLISTS);

        try (FileOutputStream fos = new FileOutputStream(ficheroPlaylist);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(playlist);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void guardarImagenPlaylist(Playlist playlist, Uri uri) {
        File carpetaImagenes = new File(context.getFilesDir().getAbsolutePath() + "/" + CARPETA_IMAGENES_PLAYLIST);
        if (!carpetaImagenes.exists())
            carpetaImagenes.mkdir();

        File ficherioImagen = new File(carpetaImagenes, playlist.getNombreFichero());

        Log.d(TAG, "Ruta fichero imagen guardada: " + ficherioImagen.getAbsolutePath());

        try (InputStream is = context.getContentResolver().openInputStream(uri);
             OutputStream fos = new FileOutputStream(ficherioImagen)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public File getImagenPlaylist(Playlist playlist) {
        File carpetaImagenes = new File(context.getFilesDir().getAbsolutePath() + "/" + CARPETA_IMAGENES_PLAYLIST);
        if (!carpetaImagenes.exists())
            return null;

        File[] imagenes = carpetaImagenes.listFiles();
        Log.i(TAG, "Número de imágenes: " + imagenes.length);

        File ficherioImagen = new File(carpetaImagenes, playlist.getNombreFichero());
        if (!ficherioImagen.exists())
            return null;

        Log.d(TAG, "Ruta fichero imagen: " + ficherioImagen.getAbsolutePath());

        return ficherioImagen;
    }

    public Playlist buscarPlaylistPorIndice(int indice) {
        if (playlists == null)
            leerPlaylists();

        try {
            return playlists.get(indice);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Indice de playlist inválido");
            return null;
        }
    }

    public void eliminarPlaylist(Playlist playlist) {
        File carpetaPlaylist = new File(context.getFilesDir(), CARPETA_PLAYLISTS);
        File ficheroPlaylist = new File(carpetaPlaylist, playlist.getNombreFichero() + EXTENSION_PLAYLISTS);

        ficheroPlaylist.delete();

        playlists.remove(playlist);
    }
}
