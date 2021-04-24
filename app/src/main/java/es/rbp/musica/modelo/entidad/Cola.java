package es.rbp.musica.modelo.entidad;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Cola implements Serializable {

    public static final long serialVersionUID = 2L;

    public static final int REPRODUCCION_LINEAL = 0;
    public static final int REPRODUCCION_ALEATORIA = 1;

    public static final int REPETICION_EN_BUCLE = 2;
    public static final int REPETICION_UNA_VEZ = 3;

    private static final int SIN_INDICE = -1;

    private List<Cancion> listaCanciones;

    private Set<Integer> indices;
    private Set<Integer> siguientesIndices;

    private Cancion cancionActual;
    private Cancion cancionAnterior;
    private Cancion siguienteCancion;

    private int indice;
    private int siguienteIndice;
    private int modoReproduccion;
    private int modoRepeticion;

    public Cola(List<Cancion> listaCanciones, Set<Integer> indices, Set<Integer> siguientesIndices, Cancion cancionAnterior, Cancion cancionActual, Cancion siguienteCancion,
                int indice, int modoReproduccion, int modoRepeticion) {
        this.listaCanciones = listaCanciones;
        this.indices = indices;
        this.siguientesIndices = siguientesIndices;
        this.cancionAnterior = cancionAnterior;
        this.cancionActual = cancionActual;
        this.siguienteCancion = siguienteCancion;
        this.indice = indice;
        this.modoReproduccion = modoReproduccion;
        this.modoRepeticion = modoRepeticion;
    }

    /**
     * Busca la {@link Cola#siguienteCancion}, actualiza la {@link Cola#cancionAnterior}, la {@link Cola#cancionActual} y el {@link Cola#indice}.
     *
     * @return Devuelve la siguiente canción o null si se ha llegado al final de la reproducción.
     */
    public Cancion siguienteCancion() {
        if (modoReproduccion == REPRODUCCION_LINEAL) {
            if (indice == listaCanciones.size() - 1 && modoRepeticion == REPETICION_EN_BUCLE) {
                this.cancionAnterior = this.cancionActual;
                this.indice = 0;
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteCancion = listaCanciones.get(indice + 1);
            } else if (indice == listaCanciones.size() && modoRepeticion == REPETICION_UNA_VEZ) {
                this.cancionAnterior = null;
                this.indice = 0;
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteCancion = listaCanciones.get(indice + 1);
                return null;
            } else {
                this.indice++;
                this.cancionAnterior = this.cancionActual;
                this.cancionActual = this.siguienteCancion;
                if (indice == listaCanciones.size() - 1)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(indice + 1);
            }
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            if (indices.size() == listaCanciones.size() && modoRepeticion == REPETICION_EN_BUCLE) {
                this.cancionAnterior = this.cancionActual;
                this.indices.clear();
                this.siguientesIndices.clear();
                this.siguienteIndice = generarIndiceAleatorio(siguientesIndices);
                this.indice = siguienteIndice;
                this.indices.add(indice);
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteIndice = generarIndiceAleatorio(siguientesIndices);
                this.siguienteCancion = listaCanciones.get(siguienteIndice);
            } else if (indices.size() == listaCanciones.size() && modoRepeticion == REPETICION_UNA_VEZ) {
                this.cancionAnterior = null;
                this.indices.clear();
                this.siguientesIndices.clear();
                this.siguienteIndice = generarIndiceAleatorio(siguientesIndices);
                this.indice = siguienteIndice;
                this.indices.add(indice);
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteIndice = generarIndiceAleatorio(siguientesIndices);
                this.siguienteCancion = listaCanciones.get(siguienteIndice);
                return null;
            } else {
                this.indice = this.siguienteIndice;
                this.indices.add(indice);
                this.cancionAnterior = this.cancionActual;
                this.cancionActual = this.siguienteCancion;
                this.siguienteIndice = generarIndiceAleatorio(siguientesIndices);
                if (siguienteIndice == SIN_INDICE)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(siguienteIndice);
            }
        }

        return this.cancionActual;
    }

    public Cancion getCancionActual() {
        return cancionActual;
    }

    public Cancion cancionAnterior() {
        if (cancionAnterior == null)
            return null;

        return this.cancionActual;
    }

    private int generarIndiceAleatorio(Set<Integer> indices) {
        if (indices.size() == listaCanciones.size())
            return SIN_INDICE;

        Random r = new Random();
        int indice;
        do {
            indice = r.nextInt(listaCanciones.size());
        } while (indices.contains(indice));

        indices.add(indice);

        return indice;
    }
}