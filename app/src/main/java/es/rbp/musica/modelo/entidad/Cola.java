package es.rbp.musica.modelo.entidad;

import java.io.Serializable;
import java.util.List;

public class Cola implements Serializable {

    public static final long serialVersionUID = 2L;

    public static final int REPRODUCCION_LINEAL = 0;
    public static final int REPRODUCCION_ALEATORIA = 1;

    public static final int REPETICION_EN_BUCLE = 2;
    public static final int REPRODUCCION_UNA_VEZ = 3;

    public static final Cola COLA_NUEVA = new Cola();

    private List<String> listaCanciones;

    private static Cola cola;

    private Cancion cancionActual;
    private Cancion cancionAnterior;

    private int indice;
    private int modoReproduccion;
    private int modoRepeticion;

    private Cola() {
    }

    public static Cola getInstance() {
        if (cola == null)
            init();
        return cola;
    }

    private static void init() {

    }
}
