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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import java.nio.file.Files;

import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.modelo.entidad.Playlist;

import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_MODO_OSCURO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_NUMERO_PLAYLISTS;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_ULTIMO_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_ULTIMO_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO;

import static es.rbp.musica.modelo.AudioUtils.filtrarCancionPorNombre;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;

import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_CANCION_ACTUAL;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_CANCION_ANTERIOR;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_INDICE;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_MODO_REPETICION;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_MODO_REPRODUCCION;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_PROGRESO_ACTUAL;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_SIGUIENTE_CANCION;
import static es.rbp.musica.modelo.entidad.Cola.PROPIEDAD_SIGUIENTE_INDICE;

/**
 * @author Ricardo Bordería Pi
 * <p>
 * Esta clase se dedica al manejo de ficheros, la persistencia de los datos de la app y contiene toda la información de la aplicación.
 * Contiene instancias de:
 * <li>
 *     <ul>{@link AccesoFichero#playlists}</ul>
 *     <ul>{@link AccesoFichero#todasCanciones}</ul>
 *     <ul>{@link AccesoFichero#favoritos}</ul>
 *     <ul>{@link AccesoFichero#historial}</ul>
 *     <ul>{@link AccesoFichero#cola}</ul>
 * </li>
 */
public class AccesoFichero {

    public static final int REQUEST_PERMISO_LECTURA = 1;

    private static final String CARPETA_PLAYLISTS = "playlists/";

    private static final String EXTENSION_PLAYLISTS = ".obj";

    private static final String CARPETA_IMAGENES_PLAYLIST = CARPETA_PLAYLISTS + "img/";

    private static final String CARPETA_COLA = "cola/";

    private static final String RUTA_COLA = CARPETA_COLA + "cola.properties";
    private static final String RUTA_LISTA_CANCIONES = CARPETA_COLA + "listaCanciones.txt";
    private static final String RUTA_INDICES = CARPETA_COLA + "indices.txt";
    private static final String RUTA_SIGUIENTES_INDICES = CARPETA_COLA + "siguientesIndices.txt";
    private static final String RUTA_INDICES_ANTERIORES = CARPETA_COLA + "indicesAnteriores.txt";

    private static final String RUTA_FICHERO_AJUSTES = "ajustes.properties";
    private static final String RUTA_CARPETAS_OCULTAS = "carpetasOcultas.txt";
    private static final String RUTA_FAVORITOS = "favoritos.txt";
    private static final String RUTA_HISTORIAL = "historial.txt";

    private static final String TAG = "ACCESO_FICHERO";

    private static AccesoFichero accesoFichero;

    private List<Playlist> playlists;

    private List<Cancion> todasCanciones;

    private List<String> favoritos;
    private List<String> historial;

    private Context context;

    private Cola cola;

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
        fos.flush();
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

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proyeccion, seleccion,
                null, null);
        todasCanciones = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            int indiceAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int indiceTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indiceArtista = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indiceDatos = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int indiceDuracion = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int indiceTamano = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);

            do {
                String datos = cursor.getString(indiceDatos);
                if (new File(datos).exists()) {
                    String album = cursor.getString(indiceAlbum);
                    String nombre = cursor.getString(indiceTitle);
                    String artista = cursor.getString(indiceArtista);
                    String duracion = cursor.getString(indiceDuracion);
                    String tamano = cursor.getString(indiceTamano);

                    if (duracion == null) {
                        duracion = "0";
                        Log.d("SIN_DURACION", datos);
                    }

                    Cancion cancion = new Cancion(album, nombre, artista, datos, duracion, tamano);

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

    /**
     * Guarda los favoritos en el fichero
     */
    private void guardarFavoritos() {
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

    /**
     * Añade un favorito a la lista
     *
     * @param favorito nuevo favorito
     */
    public void anadirFavorito(String favorito) {
        favoritos.add(favorito);
        guardarFavoritos();
    }

    /**
     * Elimina un favorito de la lista
     *
     * @param favorito favorito a eliminar
     */
    public void eliminarFavorito(String favorito) {
        favoritos.remove(favorito);
        guardarFavoritos();
    }

    /**
     * Devuelve las playlists, si no existen las lee
     *
     * @return todas las playlists creadas
     */
    public List<Playlist> getPlaylists() {
        if (playlists == null)
            leerPlaylists();

        return playlists;
    }

    /**
     * Crea una {@link Playlist} nueva y actualiza el número de playlists creadas
     *
     * @param nombrePlaylist Nombre de la nueva {@link Playlist}
     * @param ajustes        Instancia de {@link Ajustes} para actualizar el número de playlists creadas
     * @return La {@link Playlist} creada
     * @throws IOException En caso de error al guardar los {@link Ajustes}
     */
    public Playlist crearPlaylist(String nombrePlaylist, Ajustes ajustes) throws IOException {
        Playlist nuevaPlaylist = new Playlist(nombrePlaylist, ajustes.getNombreFicheroNuevaPLaylist());
        this.playlists.add(nuevaPlaylist);
        ajustes.setNumPlaylists(ajustes.getNumPlaylists() + 1);

        guardarAjustes(ajustes);

        return nuevaPlaylist;
    }

    /**
     * Lee las playlists de los ficheros
     */
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

    /**
     * Devuelve la {@link Playlist} con el ínide indicado
     *
     * @param indice Índice de la {@link Playlist}
     * @return {@link Playlist} del índice indicado
     */
    public Playlist getPlaylistPorIndice(int indice) {
        if (playlists == null)
            leerPlaylists();

        return playlists.get(indice);
    }

    /**
     * Añade una {@link Playlist} y la guarda
     *
     * @param playlist {@link Playlist} nueva
     */
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

    /**
     * Almacena en un fichero la imagen de la {@link Playlist} en un fichero
     *
     * @param playlist {@link Playlist} que contiene la imagen
     * @param uri      {@link Uri} de la imagen
     */
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

    /**
     * Devuelve el {@link File} que contiene la imagen de la {@link Playlist}
     *
     * @param playlist {@link Playlist} que contiene la imagen
     * @return Un {@link File} con la imagen de la {@link Playlist}
     */
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

    /**
     * Busca una {@link Playlist} con el índice indicado
     *
     * @param indice Índice de la {@link Playlist}
     * @return {@link Playlist} del índice indicado
     */
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

    /**
     * Elimina una {@link Playlist} de la lista
     *
     * @param playlist {@link Playlist} a eliminar
     */
    public void eliminarPlaylist(Playlist playlist) {
        File carpetaPlaylist = new File(context.getFilesDir(), CARPETA_PLAYLISTS);
        File ficheroPlaylist = new File(carpetaPlaylist, playlist.getNombreFichero() + EXTENSION_PLAYLISTS);

        ficheroPlaylist.delete();

        playlists.remove(playlist);
    }

    /**
     * Devuelve el historial de búsqueda de {@link es.rbp.musica.vista.activities.BuscarActivity}, si no existe, lo lee
     *
     * @return historial de {@link es.rbp.musica.vista.activities.BuscarActivity}
     */
    public List<String> getHistorial() {
        if (historial == null)
            leerHistorial();

        return historial;
    }

    /**
     * Lee el historial de {@link es.rbp.musica.vista.activities.BuscarActivity}
     */
    private void leerHistorial() {
        historial = new ArrayList<>();
        try {
            File ficheroHistorial = new File(context.getFilesDir(), RUTA_HISTORIAL);
            if (!ficheroHistorial.exists())
                ficheroHistorial.createNewFile();

            historial = Files.readAllLines(ficheroHistorial.toPath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Guarda el historial de búsqueda de {@link es.rbp.musica.vista.activities.BuscarActivity}
     *
     * @param historial Historial de búsqueda
     */
    public void guardarHistorial(List<String> historial) {
        File ficheroHistorial = new File(context.getFilesDir(), RUTA_HISTORIAL);
        StringBuilder contenido = new StringBuilder();
        for (String estring : historial) {
            contenido.append(estring).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(ficheroHistorial)) {
            fos.write(contenido.toString().getBytes());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        this.historial = historial;
    }

    /**
     * Elimina el historial de {@link es.rbp.musica.vista.activities.BuscarActivity}
     */
    public void eliminarHistorial() {
        historial.clear();

        guardarHistorial(historial);
    }

    /**
     * Devuelve la {@link Cola}. Si no existe, la lee
     *
     * @return Instancia de {@link Cola}
     */
    public Cola getCola() {
        if (cola == null) {
            try {
                cola = leerCola();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                cola = new Cola();
            } finally {
                guardarCola(cola);
            }
        }

        return cola;
    }

    /**
     * Lee la {@link Cola} almacenada en los ficheros
     *
     * @return Una {@link Cola} con los datos de los ficheros. Una {@link Cola} con datos por defecto y sin canciones si ocurre algún error
     * @throws IOException En caso de algún error con los ficheros
     */
    private Cola leerCola() throws IOException {
        boolean todoCorrecto = true;
        File carpetaCola = new File(context.getFilesDir(), CARPETA_COLA);
        if (!carpetaCola.exists())
            carpetaCola.mkdir();

        File ficheroCola = new File(context.getFilesDir(), RUTA_COLA);
        if (!ficheroCola.exists()) {
            ficheroCola.createNewFile();
            Log.i(TAG, "Fichero cola no existe");
            todoCorrecto = false;
        }

        File ficheroCanciones = new File(context.getFilesDir(), RUTA_LISTA_CANCIONES);
        if (!ficheroCanciones.exists()) {
            ficheroCanciones.createNewFile();
            Log.i(TAG, "Fichero canciones no existe");
            todoCorrecto = false;
        }

        File ficheroIndices = new File(context.getFilesDir(), RUTA_INDICES);
        if (!ficheroIndices.exists()) {
            ficheroIndices.createNewFile();
            Log.i(TAG, "Fichero índices no existe");
            todoCorrecto = false;
        }

        File ficheroSiguientesIndices = new File(context.getFilesDir(), RUTA_SIGUIENTES_INDICES);
        if (!ficheroSiguientesIndices.exists()) {
            ficheroSiguientesIndices.createNewFile();
            Log.i(TAG, "Fichero siguientes índices no existe");
            todoCorrecto = false;
        }

        File ficheroIndicesAnteriores = new File(context.getFilesDir(), RUTA_INDICES_ANTERIORES);
        if (!ficheroIndicesAnteriores.exists()) {
            ficheroIndicesAnteriores.createNewFile();
            Log.i(TAG, "Fichero índices anteriores no existe");
            todoCorrecto = false;
        }
        Properties properties = new Properties();
        properties.load(new FileInputStream(ficheroCola));

        if (!properties.containsKey(PROPIEDAD_CANCION_ANTERIOR) || !properties.containsKey(PROPIEDAD_CANCION_ACTUAL) || !properties.containsKey(PROPIEDAD_SIGUIENTE_CANCION)
                || !properties.containsKey(PROPIEDAD_INDICE) || !properties.containsKey(PROPIEDAD_SIGUIENTE_INDICE) || !properties.containsKey(PROPIEDAD_MODO_REPRODUCCION)
                || !properties.containsKey(PROPIEDAD_MODO_REPETICION) || !properties.containsKey(PROPIEDAD_PROGRESO_ACTUAL))
            return new Cola();

        if (!todoCorrecto)
            return new Cola();

        String cancionAnteriorStr = properties.getProperty(PROPIEDAD_CANCION_ANTERIOR);
        String cancionActualStr = properties.getProperty(PROPIEDAD_CANCION_ACTUAL);
        String siguienteCancionStr = properties.getProperty(PROPIEDAD_SIGUIENTE_CANCION);
        String indiceStr = properties.getProperty(PROPIEDAD_INDICE);
        String siguienteIndiceStr = properties.getProperty(PROPIEDAD_SIGUIENTE_INDICE);
        String modoReproduccionStr = properties.getProperty(PROPIEDAD_MODO_REPRODUCCION);
        String modoRepeticionStr = properties.getProperty(PROPIEDAD_MODO_REPETICION);
        String progresoActualStr = properties.getProperty(PROPIEDAD_PROGRESO_ACTUAL);

        List<String> listaCancionesStr = Files.readAllLines(ficheroCanciones.toPath());
        List<String> listaIndices = Files.readAllLines(ficheroIndices.toPath());
        List<String> listaSiguientesIndices = Files.readAllLines(ficheroSiguientesIndices.toPath());
        List<String> listaIndicesAnteriores = Files.readAllLines(ficheroIndicesAnteriores.toPath());

        if (todasCanciones == null)
            leerCanciones();

        List<Cancion> listaCanciones = filtrarCancionesPorNombres(todasCanciones, listaCancionesStr);

        Set<Integer> indices = new HashSet<>();
        for (String indice : listaIndices) {
            indices.add(Integer.parseInt(indice));
        }
        Set<Integer> siguientesIndices = new HashSet<>();
        for (String siguienteIndice : listaSiguientesIndices) {
            siguientesIndices.add(Integer.parseInt(siguienteIndice));
        }
        Stack<Integer> indicesAnteriores = new Stack<>();
        for (String indiceAnterior : listaIndicesAnteriores) {
            indicesAnteriores.push(Integer.parseInt(indiceAnterior));
        }

        Cancion cancionAnterior = filtrarCancionPorNombre(todasCanciones, cancionAnteriorStr);
        Cancion cancionActual = filtrarCancionPorNombre(todasCanciones, cancionActualStr);
        Cancion siguienteCancion = filtrarCancionPorNombre(todasCanciones, siguienteCancionStr);

        int indice = Integer.parseInt(indiceStr);
        int siguienteIndice = Integer.parseInt(siguienteIndiceStr);

        int modoReproduccion = Integer.parseInt(modoReproduccionStr);
        int modoRepeticion = Integer.parseInt(modoRepeticionStr);

        int progresoActual = Integer.parseInt(progresoActualStr);

        Cola cola = new Cola(listaCanciones, indices, siguientesIndices, indicesAnteriores, cancionAnterior, cancionActual, siguienteCancion, indice, siguienteIndice,
                modoReproduccion, modoRepeticion, progresoActual);

        Log.i(TAG, cola.toString());
        return cola;
    }

    /**
     * Actualiza la {@link AccesoFichero#cola} y la guarda en los distintos ficheros
     *
     * @param cola {@link Cola} a guardar
     */
    public void guardarCola(Cola cola) {
        List<Cancion> listaCanciones = cola.getListaCanciones();
        Set<Integer> indices = cola.getIndices();
        Set<Integer> siguientesIndices = cola.getSiguientesIndices();
        Stack<Integer> indicesAnteriores = cola.getIndicesAnteriores();
        Cancion cancionAnterior = cola.getCancionAnterior();
        Cancion cancionActual = cola.getCancionActual();
        Cancion siguienteCancion = cola.getSiguienteCancion();
        int indice = cola.getIndice();
        int siguienteIndice = cola.getSiguienteIndice();
        int modoReproduccion = cola.getModoReproduccion();
        int modoRepeticion = cola.getModoRepeticion();
        int progresoActual = cola.getProgresoActual();

        StringBuilder listaCancionesStr = new StringBuilder();
        for (Cancion cancion : listaCanciones) {
            listaCancionesStr.append(cancion.getDatos()).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), RUTA_LISTA_CANCIONES))) {
            fos.write(listaCancionesStr.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        StringBuilder indicesStr = new StringBuilder();
        for (Integer index : indices) {
            indicesStr.append(index).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), RUTA_INDICES))) {
            fos.write(indicesStr.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        StringBuilder siguientesIndicesStr = new StringBuilder();
        for (Integer siguienteIndex : siguientesIndices) {
            siguientesIndicesStr.append(siguienteIndex).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), RUTA_SIGUIENTES_INDICES))) {
            fos.write(siguientesIndicesStr.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        StringBuilder indicesAnterioresStr = new StringBuilder();
        for (Integer indexAnterior : indicesAnteriores) {
            indicesAnterioresStr.append(indexAnterior).append("\n");
        }
        try (FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), RUTA_INDICES_ANTERIORES))) {
            fos.write(indicesStr.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        try {
            File ficheroCola = new File(context.getFilesDir(), RUTA_COLA);
            Properties properties = new Properties();
            properties.load(new FileInputStream(ficheroCola));

            try {
                properties.setProperty(PROPIEDAD_CANCION_ANTERIOR, cancionAnterior.getDatos());

            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
                properties.setProperty(PROPIEDAD_CANCION_ANTERIOR, "null");
            }

            try {
                properties.setProperty(PROPIEDAD_SIGUIENTE_CANCION, siguienteCancion.getDatos());
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
                properties.setProperty(PROPIEDAD_SIGUIENTE_CANCION, "null");
            }

            try {
                properties.setProperty(PROPIEDAD_CANCION_ACTUAL, cancionActual.getDatos());
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
                properties.setProperty(PROPIEDAD_CANCION_ACTUAL, "null");
            }

            properties.setProperty(PROPIEDAD_INDICE, String.valueOf(indice));
            properties.setProperty(PROPIEDAD_SIGUIENTE_INDICE, String.valueOf(siguienteIndice));

            properties.setProperty(PROPIEDAD_MODO_REPRODUCCION, String.valueOf(modoReproduccion));
            properties.setProperty(PROPIEDAD_MODO_REPETICION, String.valueOf(modoRepeticion));

            properties.setProperty(PROPIEDAD_PROGRESO_ACTUAL, String.valueOf(progresoActual));

            properties.store(new FileOutputStream(ficheroCola), "");
            this.cola = cola;
            Log.i(TAG, "Cola guardada: " + cola);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void eliminarFicheros() {
        eliminarArchivos(context.getFilesDir());
        eliminarArchivos(context.getCacheDir());
        logout();
    }

    public void logout() {
        accesoFichero = null;
    }

    private void eliminarArchivos(File carpeta) {
        File[] ficheros = carpeta.listFiles();
        for (File file : ficheros) {
            if (file.isDirectory() && file.listFiles().length > 0)
                eliminarArchivos(file);
            else {
                if (file.delete())
                    Log.d(TAG, "Eliminado: " + file.getAbsolutePath());
                else
                    Log.d(TAG, "No se ha eliminado: " + file.getAbsolutePath());
            }
        }
    }
}