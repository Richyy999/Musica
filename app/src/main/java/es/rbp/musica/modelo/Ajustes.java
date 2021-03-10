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

    public static final String PROPIEDAD_MODO_OSCURO = "modoOscuro";
    public static final String PROPIEDAD_FILTRO_TAMANO = "tamano";
    public static final String PROPIEDAD_FILTRO_DURACION = "duracion";

    private static final String TAG = "AJUSTES";

    private static Ajustes ajustes;

    private List<String> carpetasOcultas;

    private boolean modoOscuro;

    private int filtroTanamo;
    private int filtroDuracion;

    /**
     * Constructor por defecto de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     */
    private Ajustes() {
    }

    /**
     * Constructor de la clase. Para obtener una instancia de la clase, usar el método {@link Ajustes#getInstance(Context)}
     *
     * @param carpetasOcultas lista con las carpetas ocultas
     * @param modoOscuro      true si está en modo oscuro, false en caso contrario
     * @param filtroTanamo    valor en KB del filtro por tamaño
     * @param filtroDuracion  valor en segundos del filtro por duracion
     */
    public Ajustes(List<String> carpetasOcultas, boolean modoOscuro, int filtroTanamo, int filtroDuracion) {
        this.carpetasOcultas = carpetasOcultas;
        this.modoOscuro = modoOscuro;
        this.filtroTanamo = filtroTanamo;
        this.filtroDuracion = filtroDuracion;
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

        ajustes.filtroTanamo = 512;
        ajustes.filtroDuracion = 30;

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

    public int getFiltroTanamo() {
        return filtroTanamo;
    }

    public void setFiltroTanamo(int filtroTanamo) {
        this.filtroTanamo = filtroTanamo;
    }

    public int getFiltroDuracion() {
        return filtroDuracion;
    }

    public void setFiltroDuracion(int filtroDuracion) {
        this.filtroDuracion = filtroDuracion;
    }

    @Override
    public String toString() {
        return "Ajustes{" +
                "modoOscuro=" + modoOscuro +
                ", filtroKB=" + filtroTanamo +
                ", filtroSeg=" + filtroDuracion +
                '}';
    }
}
