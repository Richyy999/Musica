package es.rbp.musica.modelo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static es.rbp.musica.modelo.AccesoFichero.RUTA_FICHERO_AJUSTES;

public class Ajustes implements Serializable {

    public static final long serialVersionUID = 3L;

    public static final int SIN_FILTRO = -1;

    private static final String TAG = "AJUSTES";

    private static Ajustes ajustes;

    private List<String> carpetasOcultas;

    private boolean modoClaro;

    private int filtroKB;
    private int filtroSeg;

    /**
     * Constructor por defecto de la clase
     */
    private Ajustes() {
    }

    /**
     * Constructor al que se accede para generar una nueva instancia de la clase
     *
     * @param context contexto de la aplicación
     */
    private Ajustes(Context context) {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(context);
        File archivoAjustes = accesoFichero.leerFichero(RUTA_FICHERO_AJUSTES);
        if (archivoAjustes.exists())
            leerAjustes(archivoAjustes);
        else
            crearAjustes(context);
    }

    /**
     * Devuelve la instancia de la clase, si no existe, develve una nueva
     *
     * @param context contexto de la aplicación
     * @return Instancia de la clase
     */
    public static Ajustes getInstance(Context context) {
        if (ajustes == null)
            ajustes = new Ajustes(context);
        return ajustes;
    }

    /**
     * Instancia la clase a partir de la instancia almacenada en el fichero
     *
     * @param archivoAjustes fichero en el que se almacena l ainstancia de la clase
     */
    private void leerAjustes(File archivoAjustes) {
        try (FileInputStream fis = new FileInputStream(archivoAjustes);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            ajustes = (Ajustes) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * En el caso de que el archivo de ajustes no exista, crea una instancia con los valores por defecto y almacena dicha instancia
     */
    private void crearAjustes(Context context) {
        ajustes = new Ajustes();
        modoClaro = true;

        carpetasOcultas = new ArrayList<>();
        carpetasOcultas.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio");

        filtroKB = 512;
        filtroSeg = 30;

        guardarAjustes(context);
    }

    /**
     * Llama a {@link AccesoFichero} para guardar la instancia de la clase
     *
     * @param context contexto de la aplicación
     */
    public void guardarAjustes(Context context) {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(context);
        accesoFichero.guardarObjeto(this, RUTA_FICHERO_AJUSTES);
    }

    public List<String> getCarpetasOcultas() {
        return carpetasOcultas;
    }

    public void setCarpetasOcultas(List<String> carpetasOcultas) {
        this.carpetasOcultas = carpetasOcultas;
    }

    public boolean isModoClaro() {
        return modoClaro;
    }

    public void setModoClaro(boolean modoClaro) {
        this.modoClaro = modoClaro;
    }

    public int getFiltroKB() {
        return filtroKB;
    }

    public void setFiltroKB(int filtroKB) {
        this.filtroKB = filtroKB;
    }

    public int getFiltroSeg() {
        return filtroSeg;
    }

    public void setFiltroSeg(int filtroSeg) {
        this.filtroSeg = filtroSeg;
    }
}
