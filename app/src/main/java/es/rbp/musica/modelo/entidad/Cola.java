package es.rbp.musica.modelo.entidad;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Cola implements Serializable {

    public static final long serialVersionUID = 2L;

    public static final int REPRODUCCION_LINEAL = 0;
    public static final int REPRODUCCION_ALEATORIA = 1;

    public static final int REPETICION_EN_BUCLE = 2;
    public static final int REPETICION_UNA_VEZ = 3;

    public static final Cola COLA_NUEVA = new Cola();

    private static final int SIN_INDICE = -1;

    private List<Cancion> listaCanciones;

    private Set<Integer> indices;
    private Set<Integer> siguientesIndices;

    private Stack<Integer> indicesAnteriores;

    private Cancion cancionActual;
    private Cancion cancionAnterior;
    private Cancion siguienteCancion;

    private int indice;
    private int siguienteIndice;

    private int modoReproduccion;
    private int modoRepeticion;

    private int progresoActual;

    private Cola() {
    }

    public Cola(List<Cancion> listaCanciones, Set<Integer> indices, Set<Integer> siguientesIndices, Stack<Integer> indicesAnteriores,
                Cancion cancionAnterior, Cancion cancionActual, Cancion siguienteCancion, int indice, int siguienteIndice, int modoReproduccion, int modoRepeticion,
                int progresoActual) {
        this.listaCanciones = listaCanciones;

        this.indices = indices;
        this.siguientesIndices = siguientesIndices;

        this.indicesAnteriores = indicesAnteriores;

        this.cancionAnterior = cancionAnterior;
        this.cancionActual = cancionActual;
        this.siguienteCancion = siguienteCancion;

        this.indice = indice;
        this.siguienteIndice = siguienteIndice;

        this.modoReproduccion = modoReproduccion;
        this.modoRepeticion = modoRepeticion;

        this.progresoActual = progresoActual;
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
            } else if (indice == listaCanciones.size() - 1 && modoRepeticion == REPETICION_UNA_VEZ) {
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
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.indice = siguienteIndice;
                this.indices.add(indice);
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.siguienteCancion = listaCanciones.get(siguienteIndice);
            } else if (indices.size() == listaCanciones.size() && modoRepeticion == REPETICION_UNA_VEZ) {
                this.cancionAnterior = null;
                this.indices.clear();
                this.siguientesIndices.clear();
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.indice = siguienteIndice;
                this.indices.add(indice);
                this.cancionActual = listaCanciones.get(indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.siguienteCancion = listaCanciones.get(siguienteIndice);
                return null;
            } else {
                this.indicesAnteriores.add(indice);
                this.indice = this.siguienteIndice;
                this.indices.add(indice);
                this.cancionAnterior = this.cancionActual;
                this.cancionActual = this.siguienteCancion;
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                if (siguienteIndice == SIN_INDICE)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(siguienteIndice);
            }
        }

        return this.cancionActual;
    }

    /**
     * Busca la {@link Cola#cancionAnterior} y actualiza la {@link Cola#cancionActual}, la {@link Cola#siguienteCancion} y el {@link Cola#indice}
     *
     * @return Debuelve la canción anterior o null si se ha llegado al principio de la reproducción
     */
    public Cancion cancionAnterior() {
        if (cancionAnterior == null)
            return null;

        if (modoReproduccion == REPRODUCCION_LINEAL) {
            this.indice--;
            this.siguienteCancion = this.cancionActual;
            this.cancionActual = this.cancionAnterior;
            if (indice == 0)
                this.cancionAnterior = null;
            else
                this.cancionAnterior = listaCanciones.get(indice - 1);
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            this.siguientesIndices.remove(siguienteIndice);
            this.siguienteIndice = this.indice;
            this.siguienteCancion = this.cancionActual;
            this.indices.remove(indice);
            this.indice = listaCanciones.indexOf(cancionAnterior);
            this.cancionActual = this.cancionAnterior;
            if (this.indicesAnteriores.size() == 0)
                this.cancionAnterior = null;
            else
                this.cancionAnterior = listaCanciones.get(indicesAnteriores.pop());
        }

        return this.cancionActual;
    }

    /**
     * Cambia el modo de repetición
     *
     * @param modoRepeticion Debe ser {@link Cola#REPETICION_EN_BUCLE} o {@link Cola#REPETICION_UNA_VEZ}
     */
    public void cambiarModoRepeticion(int modoRepeticion) {
        this.modoRepeticion = modoRepeticion;
    }

    /**
     * Cambia el modo de reproducción
     *
     * @param modoReproduccion Debe ser {@link Cola#REPRODUCCION_LINEAL} o {@link Cola#REPRODUCCION_ALEATORIA}
     */
    public void cambiarModoReproduccion(int modoReproduccion) {
        if (this.modoReproduccion != modoReproduccion) {
            this.modoReproduccion = modoReproduccion;
            if (modoReproduccion == REPRODUCCION_LINEAL) {
                indices.clear();
                indicesAnteriores.clear();
                siguientesIndices.clear();
                if (indice == 0)
                    this.cancionAnterior = null;
                else
                    this.cancionAnterior = listaCanciones.get(indice - 1);

                if (indice == listaCanciones.size() - 1)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(indice + 1);

            } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
                this.siguientesIndices.add(indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.siguienteCancion = listaCanciones.get(siguienteIndice);
                this.cancionAnterior = null;
            }
        }
    }

    /**
     * Añade una {@link Cancion} a la {@link Cola} en una posición que no se ha reproducido todavía
     *
     * @param cancion {@link Cancion} a añadir a la {@link Cola}
     */
    public void anadirALaCola(Cancion cancion) {
        Random r = new Random();
        int indice;
        if (modoReproduccion == REPRODUCCION_LINEAL) {
            indice = r.nextInt((this.listaCanciones.size() + 1) - this.indice) + this.indice;
            this.listaCanciones.add(indice, cancion);
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            do {
                indice = r.nextInt(listaCanciones.size() + 1);
            } while (indices.contains(indice));
            this.listaCanciones.add(indice, cancion);
        }
    }

    /**
     * Añade un {@link List} de {@link Cancion} a la {@link Cola} en posiciones que no han sido reproducidas
     *
     * @param canciones {@link List} de {@link Cancion} a añadir a la {@link Cola}
     */
    public void anadirALaCola(List<Cancion> canciones) {
        Random r = new Random();
        if (modoReproduccion == REPRODUCCION_LINEAL) {
            for (Cancion cancion : canciones) {
                int indice = r.nextInt((listaCanciones.size() + 1) - this.indice) + this.indice;
                listaCanciones.add(indice, cancion);
            }
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            for (Cancion cancion : canciones) {
                int indice;
                do {
                    indice = r.nextInt(listaCanciones.size() + 1);
                } while (indices.contains(indice));
                listaCanciones.add(indice, cancion);
            }
        }
    }

    /**
     * Añade una {@link Cancion} para que sea la siguiente en reproducirse
     *
     * @param cancion {@link Cancion} a añadir
     */
    public void reproducirSiguiente(Cancion cancion) {
        if (modoReproduccion == REPRODUCCION_LINEAL)
            listaCanciones.add(indice + 1, cancion);
        else if (modoReproduccion == REPRODUCCION_ALEATORIA)
            listaCanciones.add(siguienteIndice, cancion);

        this.siguienteCancion = cancion;
    }

    /**
     * Añade un {@link List} de {@link Cancion} a la cola. La primera canción de la lista será la siguiente en reproducirse.
     * Si {@link Cola#modoReproduccion} es {@link Cola#REPRODUCCION_LINEAL}, se añadirán en orden empezando por la {@link Cola#siguienteCancion}.
     * Si {@link Cola#modoReproduccion} es {@link Cola#REPRODUCCION_ALEATORIA}, se añadirá la primera canción como {@link Cola#siguienteCancion}
     * y el resto se añadirán aleatoriamente en posiciones que no se han reproducido aún
     *
     * @param canciones Canciones a añadir
     */
    public void reproducirSiguiente(List<Cancion> canciones) {
        if (modoReproduccion == REPRODUCCION_LINEAL) {
            int indice = this.indice;
            for (Cancion cancion : canciones) {
                indice++;
                listaCanciones.add(indice, cancion);
            }
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            for (int i = 0; i < listaCanciones.size(); i++) {
                if (i == 0)
                    reproducirSiguiente(canciones.get(indice));
                else
                    anadirALaCola(canciones.get(i));
            }
        }
    }

    /**
     * Elimina una {@link Cancion} de {@link Cola#listaCanciones}
     *
     * @param indice índice de la {@link Cancion} a eliminar
     */
    public void eliminarCancion(int indice) {
        listaCanciones.remove(indice);
    }

    /**
     * Debuelve la canción que se está reproduciendo
     *
     * @return Canción que se está reproduciendo
     */
    public Cancion getCancionActual() {
        return cancionActual;
    }

    /**
     * Devuelve el progreso de la canción actual
     *
     * @return progreso de la canción actual
     */
    public int getProgresoActual() {
        return progresoActual;
    }

    /**
     * Guarda el progreso de la canción actual
     *
     * @param progresoActual progreso de la canción actual
     */
    public void setProgresoActual(int progresoActual) {
        this.progresoActual = progresoActual;
    }

    /**
     * Genera un índice aleatorio que no esté contenido en el {@link Set} de índices indicado
     *
     * @param indices {@link Set} con los índices generados hasta el momento
     * @return Índice generado aleatoriamente
     */
    private int generarSiguienteIndiceAleatorio(Set<Integer> indices) {
        if (indices.size() == listaCanciones.size())
            return SIN_INDICE;

        Random r = new Random();
        int indice;
        do {
            indice = r.nextInt(listaCanciones.size());
        } while (indices.contains(indice));

        return indice;
    }
}