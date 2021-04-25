package es.rbp.musica.modelo.entidad;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import es.rbp.musica.modelo.AccesoFichero;

/**
 * @author Ricardo Bordería Pi
 * <p>
 * Esta clase contiene toda la información necesaria para reproducir música. Contiene la lista de {@link Cancion} que se van a reproducir, el orden
 * en que se van a reproducir las canciones, si se van a reproducir en bucle o se van a reproducir una única vez. Proporciona la canción que
 * se está reproduciendo actualmente, la canción que se reproducirá cuando la {@link Cola#cancionActual} se termine y la canción que se reprodujo anteriormente.
 */
public class Cola implements Serializable {

    public static final long serialVersionUID = 2L;

    /**
     * Indica que las {@link Cancion} se reproducirán linealmente
     */
    public static final int REPRODUCCION_LINEAL = 0;
    /**
     * Indica que las {@link Cancion} se reproducirán aleatoriamente
     */
    public static final int REPRODUCCION_ALEATORIA = 1;

    /**
     * Indica que la reproducción empzará de nuevo cuando se haya reproducido la {@link Cola#listaCanciones}
     */
    public static final int REPETICION_EN_BUCLE = 2;
    /**
     * Indica que la reproducción se detendrá cuando se haya reproducido la {@link Cola#listaCanciones}
     */
    public static final int REPETICION_UNA_VEZ = 3;

    /**
     * Propiedad del archivo que referencia a la {@link Cola#cancionActual}
     */
    public static final String PROPIEDAD_CANCION_ACTUAL = "cancionActual";
    /**
     * Propiedad del archivo que referencia a la {@link Cola#cancionAnterior}
     */
    public static final String PROPIEDAD_CANCION_ANTERIOR = "cancionAnterior";
    /**
     * Propiedad del archivo que referencia a la {@link Cola#siguienteCancion}
     */
    public static final String PROPIEDAD_SIGUIENTE_CANCION = "siguienteCancion";
    /**
     * Propiedad del archivo que referencia al {@link Cola#indice}
     */
    public static final String PROPIEDAD_INDICE = "indice";
    /**
     * Propiedad del archivo que referencia al {@link Cola#siguienteIndice}
     */
    public static final String PROPIEDAD_SIGUIENTE_INDICE = "siguienteIndice";
    /**
     * Propiedad del archivo que referencia al {@link Cola#modoReproduccion}
     */
    public static final String PROPIEDAD_MODO_REPRODUCCION = "modoReproduccion";
    /**
     * Propiedad del archivo que referencia al {@link Cola#modoRepeticion}
     */
    public static final String PROPIEDAD_MODO_REPETICION = "modoRepeticion";
    /**
     * Propiedad del archivo que referencia al {@link Cola#progresoActual}
     */
    public static final String PROPIEDAD_PROGRESO_ACTUAL = "progresoActual";

    /**
     * {@link Cola} sin datos
     */
    public static final Cola COLA_VACIA = new Cola();

    /**
     * Indica que se ha alcanzado el límite de índices que se pueden generar
     */
    private static final int SIN_INDICE = -1;

    /**
     * TAG para el log
     */
    private static final String TAG = "COLA";

    /**
     * {@link List} con las {@link Cancion} que se reproducirán
     */
    private List<Cancion> listaCanciones;

    /**
     * {@link Set} que contiene los índices para la {@link Cola#REPRODUCCION_ALEATORIA}
     */
    private Set<Integer> indices;
    /**
     * {@link Set} que contiene los siguientes índices para la {@link Cola#REPRODUCCION_ALEATORIA}
     */
    private Set<Integer> siguientesIndices;

    /**
     * {@link Stack} que contiene los índices anteriores para la {@link Cola#REPRODUCCION_ALEATORIA}
     */
    private Stack<Integer> indicesAnteriores;

    /**
     * {@link Cancion} anterior a la {@link Cola#cancionActual}
     */
    private Cancion cancionActual;
    /**
     * {@link Cancion} que se está reproduciendo actualmente
     */
    private Cancion cancionAnterior;
    /**
     * {@link Cancion} que se reproducirá después de la {@link Cola#cancionActual}
     */
    private Cancion siguienteCancion;

    /**
     * Índice de la {@link Cola#listaCanciones} que corresponde a la {@link Cola#cancionActual}
     */
    private int indice;
    /**
     * Índice de la {@link Cola#listaCanciones} que corresponde a la {@link Cola#siguienteCancion}
     */
    private int siguienteIndice;

    /**
     * Modo en el que se reproducirán la {@link Cola#listaCanciones}
     * <p>
     * Puede ser {@link Cola#REPRODUCCION_LINEAL} o {@link Cola#REPRODUCCION_ALEATORIA}
     */
    private int modoReproduccion;
    /**
     * Modo en el que se repetirá o no la {@link Cola#listaCanciones}
     * <p>
     * Puede ser {@link Cola#REPETICION_UNA_VEZ} o {@link Cola#REPETICION_EN_BUCLE}
     */
    private int modoRepeticion;

    /**
     * Progreso de la {@link Cola#cancionActual} cuando se detuvo la reproducción
     */
    private int progresoActual;

    /**
     * Constructor privado para instanciar {@link Cola#COLA_VACIA}
     */
    private Cola() {
    }

    /**
     * Constructor para crear la cola. Para obtener una instancia de la clase, utilizar {@link AccesoFichero#getCola()}.
     * Este constructor está pensado para que {@link AccesoFichero} pueda instanciar la clase y seguir el patrón Singleton.
     *
     * @param listaCanciones    {@link List} con las {@link Cancion} que reproducirá la {@link Cola}
     * @param indices           {@link Set} que contiene los índices para la {@link Cola#REPRODUCCION_ALEATORIA}
     * @param siguientesIndices {@link Set} que contiene los siguientes índices para la {@link Cola#REPRODUCCION_ALEATORIA}
     * @param indicesAnteriores {@link Stack} que contiene l    os índices anteriores para la {@link Cola#REPRODUCCION_ALEATORIA}
     * @param cancionAnterior   {@link Cola#cancionAnterior}
     * @param cancionActual     {@link Cola#cancionActual}
     * @param siguienteCancion  {@link Cola#siguienteCancion}
     * @param indice            Índice de la {@link Cola#cancionActual}
     * @param siguienteIndice   Ínice de la {@link Cola#siguienteCancion}
     * @param modoReproduccion  {@link Cola#REPRODUCCION_LINEAL} para reproducir las {@link Cancion} por orden.
     *                          {@link Cola#REPRODUCCION_ALEATORIA} para reproducir las {@link Cancion} en orden aleatorio
     * @param modoRepeticion    {@link Cola#REPETICION_UNA_VEZ} para terminar la reproducción al llegar al final de la {@link Cola}.
     *                          {@link Cola#REPETICION_EN_BUCLE} para reproducir la {@link Cola} en bucle
     * @param progresoActual    {@link Cola#progresoActual}
     */
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

        Log.d(TAG, "Índice = " + this.indice);
        Log.d(TAG, "Modo reproduccion: " + this.modoReproduccion);
        Log.d(TAG, "Modo repeticion: " + this.modoRepeticion);

        if (this.siguienteCancion != null)
            Log.d(TAG, "Siguiente cancion: " + this.siguienteCancion);
        else
            Log.d(TAG, "Siguiente cancion: null");

        if (this.cancionActual != null)
            Log.d(TAG, "Cancion actual: " + this.cancionActual);
        else
            Log.d(TAG, "Cancion actual: null");

        if (this.cancionAnterior != null)
            Log.d(TAG, "Cancion anterior: " + this.cancionAnterior);
        else
            Log.d(TAG, "Cancion anterior: null");
    }

    /**
     * Crea una {@link Cola} con los datos indicados
     *
     * @param listaCanciones   {@link List} con las {@link Cancion} a reproducir
     * @param modoReproduccion {@link Cola#REPRODUCCION_LINEAL} para reproducir las {@link Cancion} por orden.
     *                         {@link Cola#REPRODUCCION_ALEATORIA} para reproducir las {@link Cancion} en orden aleatorio
     * @param modoRepeticion   {@link Cola#REPETICION_UNA_VEZ} para terminar la reproducción al llegar al final de la {@link Cola}.
     *                         {@link Cola#REPETICION_EN_BUCLE} para reproducir la {@link Cola} en bucle
     * @param indice           Índice de la {@link Cancion} que encabeza la {@link Cola}
     */
    public void crearCola(List<Cancion> listaCanciones, int modoReproduccion, int modoRepeticion, int indice) {
        if (listaCanciones.size() == 1)
            crearCola(listaCanciones.get(0));
        else {
            this.listaCanciones = new ArrayList<>();
            this.modoReproduccion = modoReproduccion;
            this.modoRepeticion = modoRepeticion;

            for (int i = 0; i < listaCanciones.size(); i++) {
                if (i == indice)
                    this.listaCanciones.add(0, listaCanciones.get(i));
                else
                    this.listaCanciones.add(listaCanciones.get(i));
            }

            this.indice = 0;

            this.indices = new HashSet<>();
            this.siguientesIndices = new HashSet<>();

            this.indicesAnteriores = new Stack<>();

            if (modoReproduccion == REPRODUCCION_ALEATORIA) {
                this.siguientesIndices.add(this.indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                this.siguienteCancion = this.listaCanciones.get(siguienteIndice);
            } else if (modoReproduccion == REPRODUCCION_LINEAL) {
                this.siguienteCancion = this.listaCanciones.get(this.indice + 1);
            }

            this.cancionActual = this.listaCanciones.get(this.indice);
            this.cancionAnterior = null;
        }
    }

    public void crearCola(Cancion cancion) {
        this.listaCanciones = new ArrayList<>();
        this.modoReproduccion = REPRODUCCION_LINEAL;
        this.modoRepeticion = REPETICION_EN_BUCLE;
        this.listaCanciones.add(cancion);
        this.cancionActual = cancion;
        this.indice = 0;
        this.indices = new HashSet<>();
        this.siguientesIndices = new HashSet<>();
        this.indicesAnteriores = new Stack<>();
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
                this.cancionActual = this.listaCanciones.get(indice);
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
                this.indicesAnteriores.push(indice);
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

        Log.d(TAG, "Índice = " + this.indice);
        Log.d(TAG, "Modo reproduccion: " + this.modoReproduccion);
        Log.d(TAG, "Modo repeticion: " + this.modoRepeticion);

        if (this.siguienteCancion != null)
            Log.d(TAG, "Siguiente cancion: " + this.siguienteCancion);
        else
            Log.d(TAG, "Siguiente cancion: null");

        if (this.cancionActual != null)
            Log.d(TAG, "Cancion actual: " + this.cancionActual);
        else
            Log.d(TAG, "Cancion actual: null");

        if (this.cancionAnterior != null)
            Log.d(TAG, "Cancion anterior: " + this.cancionAnterior);
        else
            Log.d(TAG, "Cancion anterior: null");
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
            if (indice == 0) {
                this.cancionAnterior = null;
                return null;
            }
            this.indice--;
            this.siguienteCancion = this.cancionActual;
            this.cancionActual = this.cancionAnterior;
            if (indice <= 0) {
                this.cancionAnterior = null;
                this.indice = 0;
            } else
                this.cancionAnterior = listaCanciones.get(indice - 1);
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            if (indices.size() == 0) {
                this.cancionAnterior = null;
                return null;
            }
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

        Log.d(TAG, "Índice = " + this.indice);
        Log.d(TAG, "Modo reproduccion: " + this.modoReproduccion);
        Log.d(TAG, "Modo repeticion: " + this.modoRepeticion);

        if (this.siguienteCancion != null)
            Log.d(TAG, "Siguiente cancion: " + this.siguienteCancion);
        else
            Log.d(TAG, "Siguiente cancion: null");

        if (this.cancionActual != null)
            Log.d(TAG, "Cancion actual: " + this.cancionActual);
        else
            Log.d(TAG, "Cancion actual: null");

        if (this.cancionAnterior != null)
            Log.d(TAG, "Cancion anterior: " + this.cancionAnterior);
        else
            Log.d(TAG, "Cancion anterior: null");
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
     * Guarda el progreso de la canción actual
     *
     * @param progresoActual progreso de la canción actual
     */
    public void setProgresoActual(int progresoActual) {
        this.progresoActual = progresoActual;
    }

    /**
     * Devuelve el progreso de la canción actual
     *
     * @return progreso de la canción actual
     */
    public int getProgresoActual() {
        return progresoActual;
    }

    public List<Cancion> getListaCanciones() {
        return listaCanciones;
    }

    public Set<Integer> getIndices() {
        return indices;
    }

    public Set<Integer> getSiguientesIndices() {
        return siguientesIndices;
    }

    public Stack<Integer> getIndicesAnteriores() {
        return indicesAnteriores;
    }

    public Cancion getCancionAnterior() {
        return cancionAnterior;
    }

    public Cancion getSiguienteCancion() {
        return siguienteCancion;
    }

    public int getIndice() {
        return indice;
    }

    public int getSiguienteIndice() {
        return siguienteIndice;
    }

    public int getModoReproduccion() {
        return modoReproduccion;
    }

    public int getModoRepeticion() {
        return modoRepeticion;
    }

    @Override
    public String toString() {
        return "Cola{" +
                "listaCanciones=" + listaCanciones +
                "\n, indices=" + indices +
                ", siguientesIndices=" + siguientesIndices +
                ", indicesAnteriores=" + indicesAnteriores +
                ", cancionActual=" + cancionActual +
                ", cancionAnterior=" + cancionAnterior +
                ", siguienteCancion=" + siguienteCancion +
                ", indice=" + indice +
                ", siguienteIndice=" + siguienteIndice +
                ", modoReproduccion=" + modoReproduccion +
                ", modoRepeticion=" + modoRepeticion +
                ", progresoActual=" + progresoActual +
                '}';
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