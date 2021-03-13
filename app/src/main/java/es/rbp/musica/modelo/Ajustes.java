package es.rbp.musica.modelo;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ajustes implements Serializable {

    public static final long serialVersionUID = 3L;

    public static final int SIN_FILTRO = -1;

    public static final int MAX_FILTRO_TAMANO = 2048;

    public static final int MAX_FILTRO_DURACION = 300;

    public static final String PROPIEDAD_MODO_OSCURO = "modoOscuro";

    public static final String PROPIEDAD_FILTRO_TAMANO = "tamano";
    public static final String PROPIEDAD_ULTIMO_FILTRO_TAMANO = "ultimoTamano";
    public static final String PROPIEDAD_FILTRO_DURACION = "duracion";
    public static final String PROPIEDAD_ULTIMO_FILTRO_DURACION = "ultimoDuracion";

    private static final String TAG = "AJUSTES";

    private static final int FILTRO_TAMANO_POR_DEFECTO = 512;
    private static final int FILTRO_DURACION_POR_DEFECTO = 30;

    private static Ajustes ajustes;

    private List<String> carpetasOcultas;

    private boolean modoOscuro;

    private int filtroTanamoActual;
    private int filtroDuracionActual;

    private int ultimoFiltroTamano;
    private int ultimoFiltroDuracion;

    /**
     * Constructor por defecto de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     */
    private Ajustes() {
    }

    /**
     * Constructor de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     *
     * @param carpetasOcultas      lista con las carpetas ocultas
     * @param modoOscuro           true si está en modo oscuro, false en caso contrario
     * @param filtroTanamoActual   valor en KB del filtro por tamaño
     * @param filtroDuracionActual valor en segundos del filtro por duracion
     */
    public Ajustes(List<String> carpetasOcultas, boolean modoOscuro, int filtroTanamoActual, int filtroDuracionActual,
                   int ultimoFiltroTamano, int ultimoFiltroDuracion) {
        this.carpetasOcultas = carpetasOcultas;
        this.modoOscuro = modoOscuro;
        this.filtroTanamoActual = filtroTanamoActual;
        this.filtroDuracionActual = filtroDuracionActual;
        this.ultimoFiltroTamano = ultimoFiltroTamano;
        this.ultimoFiltroDuracion = ultimoFiltroDuracion;
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
     */
    private void crearAjustes(Context context) {
        Log.i(TAG, "Ajustes nuevos");
        ajustes = new Ajustes();
        ajustes.modoOscuro = false;

        ajustes.carpetasOcultas = new ArrayList<>();
        ajustes.carpetasOcultas.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio");

        ajustes.filtroTanamoActual = FILTRO_TAMANO_POR_DEFECTO;
        ajustes.filtroDuracionActual = FILTRO_DURACION_POR_DEFECTO;

        ajustes.ultimoFiltroTamano = FILTRO_TAMANO_POR_DEFECTO;
        ajustes.ultimoFiltroDuracion = FILTRO_DURACION_POR_DEFECTO;

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

    public void actualizarFiltroTamano(int nuevoValor) {
        this.ultimoFiltroTamano = this.filtroTanamoActual;
        this.filtroTanamoActual = nuevoValor;
    }

    public void actualizarFiltroDuracion(int nuevoValor) {
        this.ultimoFiltroDuracion = this.filtroDuracionActual;
        this.filtroDuracionActual = nuevoValor;
    }

    public void anadirCarpetaOculta(String carpeta) {
        this.carpetasOcultas.add(carpeta);
    }

    public List<String> getCarpetasOcultas() {
        return carpetasOcultas;
    }

    public void setCarpetasOcultas(List<String> carpetasOcultas) {
        this.carpetasOcultas = carpetasOcultas;
    }

    public boolean isModoOscuro() {
        return modoOscuro;
    }

    public void setModoOscuro(boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
    }

    public int getFiltroTanamoActual() {
        return filtroTanamoActual;
    }

    public void setFiltroTanamoActual(int filtroTanamoActual) {
        this.filtroTanamoActual = filtroTanamoActual;
    }

    public int getFiltroDuracionActual() {
        return filtroDuracionActual;
    }

    public void setFiltroDuracionActual(int filtroDuracionActual) {
        this.filtroDuracionActual = filtroDuracionActual;
    }

    public int getUltimoFiltroTamano() {
        return ultimoFiltroTamano;
    }

    public void setUltimoFiltroTamano(int ultimoFiltroTamano) {
        this.ultimoFiltroTamano = ultimoFiltroTamano;
    }

    public int getUltimoFiltroDuracion() {
        return ultimoFiltroDuracion;
    }

    public void setUltimoFiltroDuracion(int ultimoFiltroDuracion) {
        this.ultimoFiltroDuracion = ultimoFiltroDuracion;
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
