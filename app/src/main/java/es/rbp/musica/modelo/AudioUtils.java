package es.rbp.musica.modelo;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Carpeta;

public class AudioUtils {

    private static final String TAG = "AUDIO UTILS";

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

    public static List<Cancion> filtrarCancionesPorNombres(List<Cancion> canciones, List<String> nombres) {
        List<Cancion> cancionesFiltradas = new ArrayList<>();
        for (Cancion cancion : canciones) {
            if (nombres.contains(cancion.getDatos()))
                cancionesFiltradas.add(cancion);
        }

        return cancionesFiltradas;
    }

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
}
