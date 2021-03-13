package es.rbp.musica.modelo;

import java.util.ArrayList;
import java.util.List;

import es.rbp.musica.modelo.entidad.Cancion;

public class AudioUtils {

    private static final String TAG = "Audio Utils";

    public static List<Cancion> filtrarCanciones(List<Cancion> canciones, Ajustes ajustes) {
        List<Cancion> cancionesFiltradas = new ArrayList<>();
        for (Cancion cancion : canciones) {
            if (ajustes.getFiltroTanamoActual() == Ajustes.SIN_FILTRO || cancion.getTamano() < ajustes.getFiltroTanamoActual())
                continue;
            if (ajustes.getFiltroDuracionActual() == Ajustes.SIN_FILTRO || cancion.getDuracion() < ajustes.getFiltroDuracionActual())
                continue;
            if (!ajustes.getCarpetasOcultas().contains(cancion.getCarpetaPadre()))
                cancionesFiltradas.add(cancion);
        }
        return cancionesFiltradas;
    }
}
