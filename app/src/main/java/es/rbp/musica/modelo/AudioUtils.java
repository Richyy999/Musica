package es.rbp.musica.modelo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Carpeta;

/**
 * @author Ricardo Bordería Pi
 * <p>
 * Clase para operar con {@link Cancion}
 */
public class AudioUtils {

    /**
     * Identificador de la clase para el Log
     */
    private static final String TAG = "AUDIO_UTILS";

    /**
     * Filtra las canciones según los filtros de {@link Ajustes}
     *
     * @param canciones Todas las {@link Cancion} del dispositivo
     * @param ajustes   Instancia de {@link Ajustes}
     * @return {@link List} con las {@link Cancion} que pasan el filtro
     */
    public static List<Cancion> filtrarCanciones(List<Cancion> canciones, Ajustes ajustes) {
        List<Cancion> cancionesFiltradas = new ArrayList<>();
        for (Cancion cancion : canciones) {
            boolean pasaFiltros = true;
            if (ajustes.getFiltroTanamoActual() != Ajustes.SIN_FILTRO && cancion.getTamano() < ajustes.getFiltroTanamoActual())
                pasaFiltros = false;
            if (ajustes.getFiltroDuracionActual() != Ajustes.SIN_FILTRO && cancion.getDuracion() < ajustes.getFiltroDuracionActual())
                pasaFiltros = false;
            if (ajustes.getCarpetasOcultas().contains(cancion.getCarpetaPadre()))
                pasaFiltros = false;

            if (pasaFiltros)
                cancionesFiltradas.add(cancion);
        }
        Log.i(TAG, "Canciones filtradas");
        return cancionesFiltradas;
    }

    /**
     * Filtra las {@link Cancion} y devuelve un {@link List} con las {@link Cancion} que poseen los nombres indicados
     *
     * @param canciones {@link List} con las canciones a filtar
     * @param nombres   {@link List} con los nombres de las {@link Cancion}
     * @return {@link List} con las {@link Cancion} que pasan el filtro
     */
    public static List<Cancion> filtrarCancionesPorNombres(List<Cancion> canciones, List<String> nombres) {
        List<Cancion> cancionesFiltradas = new ArrayList<>();
        for (String nombre : nombres) {
            for (Cancion cancion : canciones) {
                if (cancion.getDatos().toLowerCase().contains(nombre.toLowerCase()))
                    cancionesFiltradas.add(cancion);
            }
        }
        return cancionesFiltradas;
    }

    /**
     * Busca una {@link Cancion} con el nombre indicado
     *
     * @param canciones {@link List} con la {@link Cancion} a buscar
     * @param nombre    Nombre de la {@link Cancion} a buscar
     * @return {@link Cancion} con el nombre indicado, null si la {@link Cancion} no existe
     */
    public static Cancion filtrarCancionPorNombre(List<Cancion> canciones, String nombre) {
        for (Cancion cancion : canciones) {
            if (cancion.getDatos().equals(nombre))
                return cancion;
        }

        return null;
    }

    /**
     * Ordena las {@link Cancion} por {@link Carpeta}
     *
     * @param canciones {@link Cancion} a ordenar
     * @return {@link List} con las {@link Carpeta}
     */
    public static List<Carpeta> getCarpetas(List<Cancion> canciones) {
        List<Carpeta> todasCarpetas = new ArrayList<>();
        Map<String, List<Cancion>> mapaCarpetas = new HashMap<>();
        for (Cancion cancion : canciones) {
            if (!mapaCarpetas.containsKey(cancion.getCarpetaPadre()))
                mapaCarpetas.put(cancion.getCarpetaPadre(), new ArrayList<Cancion>());
            mapaCarpetas.get(cancion.getCarpetaPadre()).add(cancion);
        }

        for (Map.Entry<String, List<Cancion>> carpetas : mapaCarpetas.entrySet()) {
            todasCarpetas.add(new Carpeta(carpetas.getKey(), carpetas.getValue()));
        }
        return todasCarpetas;
    }

    /**
     * Filtra {@link Cancion} por nombre, artista y álbum que contengan la query
     *
     * @param canciones {@link Cancion} por filtrar
     * @param query     {@link String} con el contenido a filtrar
     * @param ajustes   Instancia de {@link Ajustes} para filtrar por nombre del archivo o de la canción
     * @return {@link List} con las {@link Cancion} que contvengan la query
     */
    public static List<Cancion> filtrarCancionesPorQuery(List<Cancion> canciones, String query, Ajustes ajustes) {
        List<Cancion> cancionesFiltradas = new ArrayList<>();

        if (query.equals("")) {
            cancionesFiltradas.addAll(canciones);
            return cancionesFiltradas;
        }

        for (Cancion cancion : canciones) {
            if (ajustes.isUtilizarNombreDeArchivo()) {
                if ((!cancion.getAlbum().equals(Cancion.ALBUM_DESCONOCIDO) && cancion.getAlbum().toLowerCase().contains(query.toLowerCase()))
                        || (!cancion.getArtista().equals(Cancion.ARTISTA_DESCONOCIDO) && cancion.getArtista().toLowerCase().contains(query.toLowerCase()))
                        || cancion.getNombreArchivo().toLowerCase().contains(query.toLowerCase()))
                    cancionesFiltradas.add(cancion);
            } else {
                if ((!cancion.getAlbum().equals(Cancion.ALBUM_DESCONOCIDO) && cancion.getAlbum().toLowerCase().contains(query.toLowerCase()))
                        || (!cancion.getArtista().equals(Cancion.ARTISTA_DESCONOCIDO) && cancion.getArtista().toLowerCase().contains(query.toLowerCase()))
                        || cancion.getNombre().toLowerCase().contains(query.toLowerCase()))
                    cancionesFiltradas.add(cancion);
            }
        }

        return cancionesFiltradas;
    }

    /**
     * Selecciona o deselecciona las {@link Cancion} indicadas
     *
     * @param canciones    {@link Cancion} a seleccionar/deseleccionar
     * @param seleccionada true si se desea seleccionar las {@link Cancion}, false en caso contrario
     */
    public static void seleccionarCanciones(List<Cancion> canciones, boolean seleccionada) {
        for (Cancion cancion : canciones) {
            cancion.setSeleccionada(seleccionada);
        }
    }

    /**
     * Comprueba si una {@link Cancion} está añadida a Favoritos
     *
     * @param accesoFichero Instancia de {@link AccesoFichero} para obtener los favoritos
     * @param cancion       {@link Cancion} a comprobar
     * @return true si la {@link Cancion} está en favoritos, false en caso contrario
     */
    public static boolean esFavorito(AccesoFichero accesoFichero, Cancion cancion) {
        boolean esFavorito = accesoFichero.getFavoritos().contains(cancion);
        Log.i(TAG, "Es favorito: " + esFavorito);
        return esFavorito;
    }

    /**
     * Muestra un Toast con el mensaje que contenga el ID
     *
     * @param context contexto de la aplicación
     * @param resID   ID del texto a mostrar
     */
    public static void showToast(Context context, int resID) {
        Toast.makeText(context, resID, Toast.LENGTH_SHORT).show();
    }

    /**
     * Obtiene la ruta de las canciones indicadas
     *
     * @param canciones Lista de canciones para obtener su ruta
     * @return Lista con las ruats de las canciones
     */
    public static List<String> getDatosCanciones(List<Cancion> canciones) {
        List<String> listaCanciones = new ArrayList<>();
        for (Cancion cancion : canciones) {
            listaCanciones.add(cancion.getDatos());
        }
        return listaCanciones;
    }
}
