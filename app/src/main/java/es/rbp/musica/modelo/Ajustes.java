package es.rbp.musica.modelo;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ajustes implements Serializable {

    public static final long serialVersionUID = 3L;

    /**
     * Valor para {@link Ajustes#filtroDuracionActual} y {@link Ajustes#filtroTanamoActual} que indica que no hay ningún filtro activo
     */
    public static final int SIN_FILTRO = -1;

    /**
     * Valor máximo del {@link Ajustes#filtroTanamoActual}
     */
    public static final int MAX_FILTRO_TAMANO = 2048;

    /**
     * Valor máximo del {@link Ajustes#filtroDuracionActual}
     */
    public static final int MAX_FILTRO_DURACION = 300;

    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#modoOscuro}
     */
    public static final String PROPIEDAD_MODO_OSCURO = "modoOscuro";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#filtroTanamoActual}
     */
    public static final String PROPIEDAD_FILTRO_TAMANO = "tamano";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#ultimoFiltroTamano}
     */
    public static final String PROPIEDAD_ULTIMO_FILTRO_TAMANO = "ultimoTamano";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#filtroDuracionActual}
     */
    public static final String PROPIEDAD_FILTRO_DURACION = "duracion";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#ultimoFiltroDuracion}
     */
    public static final String PROPIEDAD_ULTIMO_FILTRO_DURACION = "ultimoDuracion";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#utilizarNombreDeArchivo}
     */
    public static final String PROPIEDAD_UTILIZAR_NOMBRE_DE_ARCHIVO = "utilizarNombreDeArchivo";
    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#numPlaylists}
     */
    public static final String PROPIEDAD_NUMERO_PLAYLISTS = "numeroDePlaylists";

    /**
     * Propiedad del fichero properties en el que se almacena el valor del {@link Ajustes#carpetasOcultas}
     */
    private static final String[] CARPETAS_OCULTAS_POR_DEFECTO = {"/storage/emulated/0/WhatsApp/Media/WhatsApp Audio",
            "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio"};

    /**
     * Tag para el log
     */
    private static final String TAG = "AJUSTES";

    /**
     * Valor por defecto del {@link Ajustes#filtroTanamoActual}
     */
    private static final int FILTRO_TAMANO_POR_DEFECTO = 512;
    /**
     * Valor por defecto del {@link Ajustes#filtroDuracionActual}
     */
    private static final int FILTRO_DURACION_POR_DEFECTO = 30;

    /**
     * Valor por defecto de {@link Ajustes#utilizarNombreDeArchivo}
     */
    private static final boolean UTILIZAR_NOMBRE_DE_ARCHIVO_POR_DEFECTO = false;

    /**
     * Instancia única de la clase
     */
    private static Ajustes ajustes;

    /**
     * Ruta de las carpetas que se desean ocultar al usuario
     */
    private List<String> carpetasOcultas;

    /**
     * true si se desea establecer el modo oscuro de la aplicación, false para establecer el modo claro
     */
    private boolean modoOscuro;
    /**
     * true si se desea utilizar el nombre del archivo como nombre de la canción, false para utilizar el nombre definido en los metadatos
     */
    private boolean utilizarNombreDeArchivo;

    /**
     * Tamaño mínimo que debe tener una canción para ser visible para el usuario
     */
    private int filtroTanamoActual;
    /**
     * Duración mínima que debe tener una canción para ser visible para el usuario
     */
    private int filtroDuracionActual;

    /**
     * Último valor que ha tenido el {@link Ajustes#filtroTanamoActual} antes de ser modificado
     */
    private int ultimoFiltroTamano;
    /**
     * Último valor que ha tenido el {@link Ajustes#filtroDuracionActual} antes de ser modificado
     */
    private int ultimoFiltroDuracion;

    /**
     * Identificador único de cada playlist creada por el usuario, este número no debe descender nunca, incluso si se ha eliminado
     * la playlist a la que identifica, ya que es el nombre del fichero que almacenará los datos de esa playlist
     */
    private int numPlaylists;

    /**
     * Constructor por defecto de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     */
    private Ajustes() {
    }

    /**
     * Constructor de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     *
     * @param carpetasOcultas         lista con las carpetas ocultas
     * @param modoOscuro              true si está en modo oscuro, false en caso contrario
     * @param utilizarNombreDeArchivo true si se desea utilizar el nombre del archivo como nombre de la canción, false en caso contrario
     * @param filtroTanamoActual      valor en KB del filtro por tamaño
     * @param filtroDuracionActual    valor en segundos del filtro por duracion
     * @param ultimoFiltroTamano      último valor de {@link Ajustes#filtroTanamoActual}
     * @param ultimoFiltroDuracion    último valor de {@link Ajustes#filtroDuracionActual}
     */
    public Ajustes(List<String> carpetasOcultas, boolean modoOscuro, boolean utilizarNombreDeArchivo, int filtroTanamoActual, int filtroDuracionActual,
                   int ultimoFiltroTamano, int ultimoFiltroDuracion, int numPlaylists) {
        this.carpetasOcultas = carpetasOcultas;
        this.modoOscuro = modoOscuro;
        this.utilizarNombreDeArchivo = utilizarNombreDeArchivo;
        this.filtroTanamoActual = filtroTanamoActual;
        this.filtroDuracionActual = filtroDuracionActual;
        this.ultimoFiltroTamano = ultimoFiltroTamano;
        this.ultimoFiltroDuracion = ultimoFiltroDuracion;
        this.numPlaylists = numPlaylists;
        ajustes = this;
    }

    /**
     * Constructor al que se accede para generar una nueva instancia de la clase
     *
     * @param context contexto de la aplicación
     */
    private Ajustes(Context context) {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(context);
        ajustes = accesoFichero.leerAjustes();
        if (ajustes == null)
            crearAjustes(context);
    }

    /**
     * Devuelve la instancia de la clase, si no existe, develve una nueva.
     * Usar este método para obtener una instancia de la clase
     *
     * @param context contexto de la aplicación
     * @return Instancia de la clase
     */
    public static Ajustes getInstance(Context context) {
        if (ajustes == null)
            new Ajustes(context);
        Log.i(TAG, ajustes.toString());
        return ajustes;
    }

    /**
     * En el caso de que el archivo de ajustes no exista, crea una instancia con los valores por defecto y almacena dicha instancia
     *
     * @param context Contexto de la aplicación
     */
    private void crearAjustes(Context context) {
        Log.i(TAG, "Ajustes nuevos");
        ajustes = new Ajustes();
        ajustes.modoOscuro = false;
        ajustes.utilizarNombreDeArchivo = UTILIZAR_NOMBRE_DE_ARCHIVO_POR_DEFECTO;

        ajustes.carpetasOcultas = new ArrayList<>();
        ajustes.carpetasOcultas.addAll(Arrays.asList(CARPETAS_OCULTAS_POR_DEFECTO));

        ajustes.filtroTanamoActual = FILTRO_TAMANO_POR_DEFECTO;
        ajustes.filtroDuracionActual = FILTRO_DURACION_POR_DEFECTO;

        ajustes.ultimoFiltroTamano = FILTRO_TAMANO_POR_DEFECTO;
        ajustes.ultimoFiltroDuracion = FILTRO_DURACION_POR_DEFECTO;

        ajustes.numPlaylists = 0;

        guardarAjustes(context);
    }

    /**
     * Llama a {@link AccesoFichero} para guardar la instancia de la clase
     *
     * @param context contexto de la aplicación
     */
    public void guardarAjustes(Context context) {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(context);
        try {
            accesoFichero.guardarAjustes(ajustes);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Actualiza el valor por el que filtra el filtro por tamaño
     *
     * @param nuevoValor nuevo tamaño por el que se qiuere filtrar
     */
    public void actualizarFiltroTamano(int nuevoValor) {
        ajustes.ultimoFiltroTamano = this.filtroTanamoActual;
        ajustes.filtroTanamoActual = nuevoValor;
    }

    /**
     * Actualiza el valor por el que filtra el filtro por diuración
     *
     * @param nuevoValor nueva duración por la que se quere filtrar
     */
    public void actualizarFiltroDuracion(int nuevoValor) {
        ajustes.ultimoFiltroDuracion = this.filtroDuracionActual;
        ajustes.filtroDuracionActual = nuevoValor;
    }

    /**
     * Añade una carpeta a la lista de carpetas ocultas
     *
     * @param carpeta ruta de la carpeta que se quiere ocultar
     */
    public void anadirCarpetaOculta(String carpeta) {
        this.carpetasOcultas.add(carpeta);
    }

    /**
     * Elimina los ajustes actuales y se crean unos nuevos desde cero
     *
     * @param context Contexto de la aplicación
     */
    public void restablecerAjustes(Context context) {
        ajustes = null;
        crearAjustes(context);
    }

    /**
     * Guarda y cierra la instancia de los ajustes
     *
     * @param context contexto de la aplicación
     */
    public void logout(Context context) {
        try {
            AccesoFichero.getInstance(context).guardarAjustes(ajustes);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            ajustes = null;
        }
    }

    /**
     * Devuelve las {@link Ajustes#carpetasOcultas}
     *
     * @return Lista con la ruta de las carpetas ocultas
     */
    public List<String> getCarpetasOcultas() {
        return carpetasOcultas;
    }

    /**
     * Devuelve el valor de {@link Ajustes#numPlaylists} en formato {@link String}
     *
     * @return {@link String} con el valor de {@link Ajustes#numPlaylists}
     */
    public String getNombreFicheroNuevaPLaylist() {
        return String.valueOf(numPlaylists);
    }

    /**
     * Devuelve {@link Ajustes#numPlaylists}
     *
     * @return {@link Ajustes#numPlaylists}
     */
    public int getNumPlaylists() {
        return numPlaylists;
    }

    /**
     * Almacena y actualiza el valor de {@link Ajustes#numPlaylists}
     *
     * @param numPlaylists nuevo valor mayor que {@link Ajustes#numPlaylists}
     */
    public void setNumPlaylists(int numPlaylists) {
        if (numPlaylists > this.numPlaylists)
            this.numPlaylists = numPlaylists;
    }

    /**
     * Actualiza las {@link Ajustes#carpetasOcultas}
     *
     * @param carpetasOcultas Ruta de las nuevas {@link Ajustes#carpetasOcultas}
     */
    public void setCarpetasOcultas(List<String> carpetasOcultas) {
        this.carpetasOcultas = carpetasOcultas;
    }

    /**
     * Devuelve el valor del {@link Ajustes#modoOscuro}
     *
     * @return {@link Ajustes#modoOscuro}
     */
    public boolean isModoOscuro() {
        return modoOscuro;
    }

    /**
     * Actualiza el valor del {@link Ajustes#modoOscuro}
     *
     * @param modoOscuro true si se desea mostrar el modo oscuro, false si se desea mostrar el modo claro
     */
    public void setModoOscuro(boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
    }

    /**
     * Devuelve el valor de {@link Ajustes#utilizarNombreDeArchivo}
     *
     * @return {@link Ajustes#utilizarNombreDeArchivo}
     */
    public boolean isUtilizarNombreDeArchivo() {
        return utilizarNombreDeArchivo;
    }

    /**
     * Actualiza el valor de {@link Ajustes#utilizarNombreDeArchivo}
     *
     * @param utilizarNombreDeArchivo true si se desea utilizar el nombre del fichero como nombre de la canción,
     *                                false si se desea utilizar el nombre de los metadadtos
     */
    public void setUtilizarNombreDeArchivo(boolean utilizarNombreDeArchivo) {
        this.utilizarNombreDeArchivo = utilizarNombreDeArchivo;
    }

    /**
     * Devuelve el valor de {@link Ajustes#filtroTanamoActual}
     *
     * @return {@link Ajustes#filtroTanamoActual}
     */
    public int getFiltroTanamoActual() {
        return filtroTanamoActual;
    }

    /**
     * Devuelve el valor de {@link Ajustes#filtroDuracionActual}
     *
     * @return {@link Ajustes#filtroDuracionActual}
     */
    public int getFiltroDuracionActual() {
        return filtroDuracionActual;
    }

    /**
     * Devuelve el valor de {@link Ajustes#ultimoFiltroTamano}
     *
     * @return {@link Ajustes#ultimoFiltroTamano}
     */
    public int getUltimoFiltroTamano() {
        return ultimoFiltroTamano;
    }

    /**
     * Devuelve el valor de {@link Ajustes#ultimoFiltroDuracion}
     *
     * @return {@link Ajustes#ultimoFiltroDuracion}
     */
    public int getUltimoFiltroDuracion() {
        return ultimoFiltroDuracion;
    }

    @Override
    public String toString() {
        return "Ajustes{" +
                "carpetasOcultas=" + carpetasOcultas +
                ", modoOscuro=" + modoOscuro +
                ", filtroTanamoActual=" + filtroTanamoActual +
                ", filtroDuracionActual=" + filtroDuracionActual +
                ", ultimoFiltroTamano=" + ultimoFiltroTamano +
                ", ultimoFiltroDuracion=" + ultimoFiltroDuracion +
                '}';
    }
}
