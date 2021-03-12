package es.rbp.musica.modelo;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import es.rbp.musica.modelo.entidad.Cancion;

public class AccesoFichero {

    public static final int REQUEST_PERMISO_LECTURA = 1;

    public static final String RUTA_FICHERO_AJUSTES = "ajustes.obj";
    public static final String RUTA_FICHERO_COLA = "cola.obj";
    public static final String RUTA_FICHERO_PLAYLISTS = "playlists.obj";

    private static final String TAG = "ACCESO_FICHARO";

    private static AccesoFichero accesoFichero;

    private List<Cancion> todasCanciones;

    private Context context;

    private AccesoFichero(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AccesoFichero getInstance(Context context) {
        if (accesoFichero == null)
            accesoFichero = new AccesoFichero(context);
        return accesoFichero;
    }

    /**
     * Lee y devuelve el archivo indicado
     *
     * @param ruta nombre del archivo
     * @return fichero con la ruta indicada
     */
    public File leerFichero(String ruta) {
        return new File(context.getFilesDir().getAbsolutePath(), ruta);
    }

    /**
     * Guarda un objeto en el archivo con la ruta indicada
     *
     * @param objeto objeto a guardar
     * @param ruta   nombre del archivo en el que guardar el objeto
     */
    public void guardarObjeto(Object objeto, String ruta) {
        File archivo = new File(context.getFilesDir().getAbsolutePath(), ruta);
        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public List<Cancion> getTodasCanciones() {
        if (todasCanciones == null)
            leerCanciones();
        return todasCanciones;
    }

    private void leerCanciones() {
        String seleccion = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + " != 0";
        String[] proyeccion = {
                MediaStore.Audio.Media.ARTIST
        };
    }
}
