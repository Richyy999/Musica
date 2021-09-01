package es.rbp.musica.modelo.entidad;

import android.util.Log;

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

    public static final int RESULTADO_COLA_ELIMINADA = -1;
    public static final int RESULTADO_COLA_TERMINADA = -2;
    public static final int RESULTADO_CANCION_ACTUAL_ELIMINADA = -3;
    public static final int RESULTADO_SIN_CAMBIOS = -4;

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
    private final List<Cancion> listaCanciones;

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
     * {@link Cancion} que se está reproduciendo actualmente
     */
    private Cancion cancionActual;
    /**
     * {@link Cancion} anterior a la {@link Cola#cancionActual}
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
     * Puede ser:
     * <ul>
     *     <li>{@link Cola#REPRODUCCION_LINEAL}</li>
     *     <li>{@link Cola#REPRODUCCION_ALEATORIA}</li>
     * </ul>
     */
    private int modoReproduccion;
    /**
     * Modo en el que se repetirá o no la {@link Cola#listaCanciones}
     * <p>
     * Puede ser:
     * <ul>
     *     <li>{@link Cola#REPETICION_UNA_VEZ}</li>
     *     <li>{@link Cola#REPETICION_EN_BUCLE}</li>
     * </ul>
     */
    private int modoRepeticion;

    /**
     * Progreso de la {@link Cola#cancionActual} cuando se detuvo la reproducción
     */
    private int progresoActual;

    /**
     * Boolean que indica si la {@link Cola#cancionActual} se está reproduciendo o no
     */
    private boolean estaReproduciendo;

    /**
     * Constructor para crear {@link Cola} con valores por defecto. Para obtener una instancia de la clase utilizar {@link AccesoFichero#getCola()}
     */
    public Cola() {
        this.listaCanciones = new ArrayList<>();

        this.indices = new HashSet<>();
        this.siguientesIndices = new HashSet<>();
        this.indicesAnteriores = new Stack<>();

        this.indice = 0;
        this.siguienteIndice = SIN_INDICE;

        this.modoReproduccion = REPRODUCCION_LINEAL;
        this.modoRepeticion = REPETICION_EN_BUCLE;

        Log.i(TAG, this.toString());
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
     * Busca la {@link Cola#siguienteCancion}, actualiza la {@link Cola#cancionAnterior}, la {@link Cola#cancionActual} y el {@link Cola#indice}.
     *
     * @return Devuelve la siguiente canción o null si se ha llegado al final de la reproducción.
     */
    public Cancion siguienteCancion() {
        if (listaCanciones.size() == 1) {
            if (modoRepeticion == REPETICION_EN_BUCLE)
                return cancionActual;
            else if (modoRepeticion == REPETICION_UNA_VEZ)
                return null;
        }
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
                if (!indicesAnteriores.contains(indice))
                    this.indicesAnteriores.push(indice);
                this.indice = this.siguienteIndice;
                this.indices.add(indice);
                this.cancionAnterior = this.cancionActual;
                this.cancionActual = this.siguienteCancion;
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                if (siguienteIndice == SIN_INDICE)
                    this.siguienteCancion = null;
                else {
                    this.siguienteCancion = listaCanciones.get(siguienteIndice);
                    this.siguientesIndices.add(siguienteIndice);
                }
            }
        }

        Log.d(TAG, "Índice = " + this.indice);
        Log.d(TAG, "Tamaño Índices: " + this.indices.size());
        for (Integer indice : indices) {
            Log.d(TAG, "Índice actual: " + indice);
        }
        Log.d(TAG, "Tamaño Índices anteriores: " + this.indicesAnteriores.size());
        for (Integer indiceAnterior : indicesAnteriores) {
            Log.d(TAG, "Índice anterior: " + indiceAnterior);
        }
        Log.d(TAG, "Tamaño Siguientes índices: " + this.siguientesIndices.size());
        for (Integer siguienteIndice : siguientesIndices) {
            Log.d(TAG, "Siguiente índice: " + siguienteIndice);
        }
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

        if (listaCanciones.size() == 1) {
            if (modoRepeticion == REPETICION_EN_BUCLE)
                return cancionActual;
            else if (modoRepeticion == REPETICION_UNA_VEZ)
                return null;
        }

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
            this.indicesAnteriores.pop();
            this.siguientesIndices.remove(siguienteIndice);
            this.siguienteIndice = this.indice;
            this.siguienteCancion = this.cancionActual;
            this.indices.remove(indice);
            this.indice = listaCanciones.indexOf(cancionAnterior);
            this.cancionActual = this.cancionAnterior;
            if (this.indicesAnteriores.size() == 0)
                this.cancionAnterior = null;
            else
                this.cancionAnterior = listaCanciones.get(indicesAnteriores.peek());
        }

        Log.d(TAG, "Índice = " + this.indice);
        Log.d(TAG, "Tamaño Índices: " + this.indices.size());
        for (Integer indice : indices) {
            Log.d(TAG, "Índice actual: " + indice);
        }
        Log.d(TAG, "Tamaño Índices anteriores: " + this.indicesAnteriores.size());
        for (Integer indiceAnterior : indicesAnteriores) {
            Log.d(TAG, "Índice anterior: " + indiceAnterior);
        }
        Log.d(TAG, "Tamaño Siguientes índices: " + this.siguientesIndices.size());
        for (Integer siguienteIndice : siguientesIndices) {
            Log.d(TAG, "Siguiente índice: " + siguienteIndice);
        }
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
     * Elimina todas las canciones de la {@link Cola} y reestablece los índices
     */
    public void eliminarCola() {
        this.listaCanciones.clear();

        this.indice = 0;
        this.siguienteIndice = SIN_INDICE;

        this.cancionActual = null;
        this.cancionAnterior = null;
        this.siguienteCancion = null;

        this.indices.clear();
        this.siguientesIndices.clear();
        this.indicesAnteriores.clear();
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
                if (indice == 0)
                    this.cancionAnterior = null;
                else
                    this.cancionAnterior = listaCanciones.get(indice - 1);

                if (indice == listaCanciones.size() - 1)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(indice + 1);

            } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
                indices.clear();
                indicesAnteriores.clear();
                siguientesIndices.clear();
                this.indices.add(indice);
                this.siguientesIndices.add(indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(siguientesIndices);
                this.siguientesIndices.add(siguienteIndice);
                try {
                    this.siguienteCancion = listaCanciones.get(siguienteIndice);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, e.toString());
                }
                this.cancionAnterior = null;

            }
        }
    }

    /**
     * Elimina la {@link Cola} actual y crea una nueva con las canciones indicadas, empezando por la canción en el índice indicado
     *
     * @param canciones     {@link List} de {@link Cancion} con las canciones de la nueva {@link Cola}
     * @param indiceCancion Índice que indica cuál es la primera {@link Cancion} de la nueva {@link Cola}
     */
    public void crearCola(List<Cancion> canciones, int indiceCancion) {
        eliminarCola();
        for (int i = 0; i < canciones.size(); i++) {
            if (i != indiceCancion)
                listaCanciones.add(canciones.get(i));
        }

        this.listaCanciones.add(0, canciones.get(indiceCancion));
        this.cancionActual = canciones.get(indiceCancion);
    }

    /**
     * Elimina la {@link Cola} actual y crea una nueva con la {@link Cancion} indicada
     *
     * @param cancion {@link Cancion} con la que se crea la nueva cola
     */
    public void crearCola(Cancion cancion) {
        eliminarCola();
        this.listaCanciones.add(cancion);
        this.cancionActual = cancion;
        if (this.modoReproduccion == REPRODUCCION_ALEATORIA) {
            this.indices.add(this.indice);
            this.siguientesIndices.add(this.indice);
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
            indice = r.nextInt((this.listaCanciones.size() + 1) - (this.indice + 1)) + (this.indice + 1);
            if (indice == 0)
                indice++;
            this.listaCanciones.add(indice, cancion);
            if (listaCanciones.size() == 1)
                cancionActual = listaCanciones.get(indice);
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            do {
                indice = r.nextInt(listaCanciones.size() + 1);
            } while (indices.contains(indice));
            this.listaCanciones.add(indice, cancion);
            if (listaCanciones.size() == 1)
                cancionActual = listaCanciones.get(indice);
        }

        Log.d(TAG, "Canción añadida " + cancion.toString());
    }

    /**
     * Añade un {@link List} de {@link Cancion} a la {@link Cola} en posiciones que no han sido reproducidas
     *
     * @param canciones {@link List} de {@link Cancion} a añadir a la {@link Cola}
     */
    public void anadirALaCola(List<Cancion> canciones) {
        for (Cancion cancion : canciones) {
            anadirALaCola(cancion);
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
        else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            listaCanciones.add(siguienteIndice, cancion);
            siguienteCancion = cancion;
        }
        this.siguienteCancion = cancion;
    }

    /**
     * Añade un {@link List} de {@link Cancion} a la cola. La primera canción de la lista será la siguiente en reproducirse.
     * Si {@link Cola#modoReproduccion} es {@link Cola#REPRODUCCION_LINEAL}, se añadirán en orden empezando por la {@link Cola#siguienteCancion}.
     * Si {@link Cola#modoReproduccion} es {@link Cola#REPRODUCCION_ALEATORIA}, se añadirá la primera canción como {@link Cola#siguienteCancion}
     * y el resto se añadirán aleatoriamente en posiciones que no se han reproducido aún
     *
     * @param canciones Canciones a añadir
     * @param indice    Índice de la siguiente canción
     */
    public void reproducirSiguiente(List<Cancion> canciones, int indice) {
        for (int i = 0; i <= canciones.size(); i++) {
            Cancion cancion = canciones.get(i);
            if (i == indice)
                reproducirSiguiente(cancion);
            else
                anadirALaCola(cancion);
        }
    }

    /**
     * Elimina una {@link Cancion} de la {@link Cola#listaCanciones} y devuelve el resultado de la operación
     *
     * @param indice índice de la {@link Cancion} a eliminar
     * @return Resultado de la operación. Puede ser:
     * <ul>
     *     <li>{@link Cola#RESULTADO_COLA_ELIMINADA}</li>
     *     <li>{@link Cola#RESULTADO_CANCION_ACTUAL_ELIMINADA}</li>
     *     <li>{@link Cola#RESULTADO_COLA_TERMINADA}</li>
     *     <li>{@link Cola#RESULTADO_SIN_CAMBIOS}</li>
     * </ul>
     */
    public int eliminarCancion(int indice) {
        if (listaCanciones.size() == 1) {
            eliminarCola();
            return RESULTADO_COLA_ELIMINADA;
        }

        if (modoReproduccion == REPRODUCCION_LINEAL) {
            // Si la canción que se elimina es la primera y la canción actual
            if (indice == 0 && indice == this.indice) {
                listaCanciones.remove(indice);
                this.cancionActual = listaCanciones.get(this.indice);
                this.siguienteCancion = listaCanciones.get(1);
                return RESULTADO_CANCION_ACTUAL_ELIMINADA;
                // Si la canción que se elimina es la primera canción, pero no es la canción actual
            } else if (indice == 0) {
                listaCanciones.remove(indice);
                this.indice--;
                if (this.indice == 0)
                    this.cancionAnterior = null;
                else
                    this.cancionAnterior = listaCanciones.get(this.indice - 1);
                // Si la canción que se elimina es la última y la canción actual
            } else if (indice == listaCanciones.size() - 1 && indice == this.indice) {
                listaCanciones.remove(indice);
                this.indice = 0;
                this.cancionActual = listaCanciones.get(this.indice);
                this.siguienteCancion = listaCanciones.get(this.indice + 1);
                if (modoRepeticion == REPETICION_UNA_VEZ) {
                    this.cancionAnterior = null;
                    return RESULTADO_COLA_TERMINADA;
                } else if (modoRepeticion == REPETICION_EN_BUCLE) {
                    this.cancionAnterior = listaCanciones.get(listaCanciones.size() - 1);
                    return RESULTADO_CANCION_ACTUAL_ELIMINADA;
                }
                // Si la canción que se elimina es la última canción, pero no es la canción actual
            } else if (indice == listaCanciones.size() - 1) {
                listaCanciones.remove(indice);
                if (this.indice == listaCanciones.size() - 1)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(this.indice + 1);
                // Si la canción que se elimina está en medio de la cola y es anterior a la canción actual
            } else if (indice < this.indice) {
                listaCanciones.remove(indice);
                this.indice--;
                this.cancionAnterior = listaCanciones.get(this.indice - 1);
                // Si la canción que se elimina está en medio de la cola y es posterior a la canción actual
            } else if (indice > this.indice) {
                listaCanciones.remove(indice);
                this.siguienteCancion = listaCanciones.get(this.indice + 1);
                // Si la canción que se elimina está en el medio de la cola y es la canción actual
            } else {
                listaCanciones.remove(indice);
                this.cancionActual = this.siguienteCancion;
                if (this.indice == listaCanciones.size() - 1)
                    this.siguienteCancion = null;
                else
                    this.siguienteCancion = listaCanciones.get(this.indice + 1);
                return RESULTADO_CANCION_ACTUAL_ELIMINADA;
            }
        } else if (modoReproduccion == REPRODUCCION_ALEATORIA) {
            // Si es la canción actual y la última
            if (indice == this.indice && this.indices.size() == listaCanciones.size()) {
                listaCanciones.remove(indice);
                this.indices.clear();
                this.siguientesIndices.clear();
                this.siguienteIndice = generarSiguienteIndiceAleatorio(this.siguientesIndices);
                this.siguientesIndices.add(this.siguienteIndice);
                this.indice = this.siguienteIndice;
                this.indices.add(this.indice);
                this.cancionActual = listaCanciones.get(this.indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(this.siguientesIndices);
                if (this.siguienteIndice == SIN_INDICE)
                    this.siguienteCancion = null;
                else {
                    this.siguienteCancion = listaCanciones.get(this.siguienteIndice);
                    this.siguientesIndices.add(this.siguienteIndice);
                }
                if (this.modoRepeticion == REPETICION_EN_BUCLE)
                    return RESULTADO_CANCION_ACTUAL_ELIMINADA;
                else if (this.modoRepeticion == REPETICION_UNA_VEZ) {
                    this.cancionAnterior = null;
                    return RESULTADO_COLA_TERMINADA;
                }
            }
            // Si es la canción actual
            else if (indice == this.indice) {
                listaCanciones.remove(indice);
                this.cancionActual = this.siguienteCancion;
                this.indices = eliminarIndice(this.indices, indice);
                this.indice = this.siguienteIndice - 1;
                this.indices.add(this.indice);
                this.siguientesIndices = eliminarIndice(this.siguientesIndices, indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(this.siguientesIndices);
                this.siguientesIndices.add(this.siguienteIndice);
                this.siguienteCancion = listaCanciones.get(this.siguienteIndice);

                return RESULTADO_CANCION_ACTUAL_ELIMINADA;
            }
            // Si la canción es la siguiente canción
            else if (indice == this.siguienteIndice) {
                listaCanciones.remove(indice);
                this.siguientesIndices.remove(indice);
                this.siguienteIndice = generarSiguienteIndiceAleatorio(this.siguientesIndices);
                if (this.siguienteIndice == SIN_INDICE)
                    this.siguienteCancion = null;
                else {
                    this.siguientesIndices.add(this.siguienteIndice);
                    this.siguienteCancion = listaCanciones.get(this.siguienteIndice);
                }
                if (this.indice == listaCanciones.size())
                    this.indice--;
            }
            // Si la canción es anterior a la canción actual
            else if (this.indicesAnteriores.contains(indice)) {
                listaCanciones.remove(indice);
                this.indice--;
                this.siguienteIndice--;
                this.indicesAnteriores = eliminarIndice(this.indicesAnteriores, indice);
                this.indices = eliminarIndice(this.indices, indice);
                this.siguientesIndices = eliminarIndice(this.siguientesIndices, indice);
                if (this.indicesAnteriores.size() == 0)
                    this.cancionAnterior = null;
                else
                    this.cancionAnterior = listaCanciones.get(this.indicesAnteriores.pop());
            }
        }

        return RESULTADO_SIN_CAMBIOS;
    }

    /**
     * Devuelve la canción que se está reproduciendo
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
     * Actualiza el valor de {@link Cola#estaReproduciendo}
     *
     * @param estaReproduciendo true si se está reproduciendo la {@link Cola#cancionActual}, false en cas contrario
     */
    public void setEstaReproduciendo(boolean estaReproduciendo) {
        this.estaReproduciendo = estaReproduciendo;
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

    public boolean seEstaReproduciendo() {
        return estaReproduciendo;
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
     * @return Índice generado aleatoriamente. {@link Cola#SIN_INDICE} en caso de que no sea posible generar un índice nuevo
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

    /**
     * Elimina el índice indicado al set y resta uno a los valores posteriores al índice
     *
     * @param coleccion Set del que se desea eliminar el índice
     * @param indice    Índice a eliminar
     * @return Set pasada por parámetro con los valores actualizados
     */
    private Set<Integer> eliminarIndice(Set<Integer> coleccion, int indice) {
        Set<Integer> setNuevo = new HashSet<>();
        for (int indiceActual : coleccion) {
            if (indiceActual > indice)
                setNuevo.add(indiceActual - 1);
            else if (indiceActual < indice)
                setNuevo.add(indiceActual);
        }
        return setNuevo;
    }

    /**
     * Elimina el indice indicado a la pila y resta uno a los valores superiores al índice
     *
     * @param stack  pila con el índice a eliminar
     * @param indice índice a eliminar
     * @return pila nueva con los valores actualizados
     */
    private Stack<Integer> eliminarIndice(Stack<Integer> stack, int indice) {
        Stack<Integer> stackNueva = new Stack<>();
        for (int indiceActual : stack) {
            if (indiceActual > indice)
                stackNueva.push(indiceActual - 1);
            else if (indiceActual < indice)
                stackNueva.push(indiceActual);
        }
        return stackNueva;
    }
}