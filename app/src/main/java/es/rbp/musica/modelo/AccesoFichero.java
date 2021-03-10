package es.rbp.musica.modelo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.PROPIEDAD_MODO_OSCURO;

public class AccesoFichero {

    public static final int REQUEST_PERMISO_LECTURA = 1;

    private static final String RUTA_FICHERO_AJUSTES = "ajustes.properties";
    private static final String RUTA_CARPETAS_OCULTAS = "carpetasOcultas.txt";

    private static final String TAG = "ACCESO_FICHARO";

    private static AccesoFichero accesoFichero;

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
     * Lee los ficheros que contienen la información de los ajustes y crean una instancia de la clase con los datos de los ficheros.
     *
     * @return Instancia de {@link Ajustes}, null si hay algún fallo
     */
    public Ajustes leerAjustes() {
        File ficheroProperties = new File(context.getFilesDir(), RUTA_FICHERO_AJUSTES);
        if (!ficheroProperties.exists()) {
            Log.d(TAG, "Ajustes properties no existe");
            return null;
        }
        File ficheroCarpetas = new File(context.getFilesDir(), RUTA_CARPETAS_OCULTAS);
        if (!ficheroCarpetas.exists()) {
            Log.d(TAG, "Ajustes carpetas ocultas no existe");
            return null;
        }

        Properties properties = new Properties();
        List<String> carpetasOcultas;
        try {
            properties.load(new FileInputStream(ficheroProperties));
            carpetasOcultas = Files.readAllLines(ficheroCarpetas.toPath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        if (!properties.containsKey(PROPIEDAD_MODO_OSCURO) || !properties.containsKey(PROPIEDAD_FILTRO_TAMANO)
                || !properties.containsKey(PROPIEDAD_FILTRO_DURACION)) {
            Log.d(TAG, "Ajustes properties corrupto");
            return null;
        }
        boolean mosoOscuro = Boolean.parseBoolean(properties.getProperty(PROPIEDAD_MODO_OSCURO));
        int filtroTamano = Integer.parseInt(properties.getProperty(PROPIEDAD_FILTRO_TAMANO));
        int filtroDuracion = Integer.parseInt(properties.getProperty(PROPIEDAD_FILTRO_DURACION));

        return new Ajustes(carpetasOcultas, mosoOscuro, filtroTamano, filtroDuracion);
    }

    public void guardarAjustes(Ajustes ajustes) throws IOException {
        String modoOscuro = String.valueOf(ajustes.isModoOscuro());
        String filtroTamano = String.valueOf(ajustes.getFiltroTanamo());
        String filtroDuracion = String.valueOf(ajustes.getFiltroDuracion());

        File ficheroProperties = new File(context.getFilesDir(), RUTA_FICHERO_AJUSTES);
        if (!ficheroProperties.exists())
            ficheroProperties.createNewFile();

        Properties properties = new Properties();
        properties.load(new FileInputStream(ficheroProperties));
        properties.setProperty(PROPIEDAD_MODO_OSCURO, modoOscuro);
        properties.setProperty(PROPIEDAD_FILTRO_TAMANO, filtroTamano);
        properties.setProperty(PROPIEDAD_FILTRO_DURACION, filtroDuracion);
        properties.store(new FileOutputStream(ficheroProperties), "");

        File ficheroCarpetas = new File(context.getFilesDir(), RUTA_CARPETAS_OCULTAS);
        StringBuilder carpetasOcultasBuilder = new StringBuilder();
        for (String carpeta : ajustes.getCarpetasOcultas()) {
            carpetasOcultasBuilder.append(carpeta).append("\n");
        }
        String carpetasOcultas = carpetasOcultasBuilder.toString();
        Log.d(TAG, "CARPETAS OCULTAS:\n" + carpetasOcultas);

        FileOutputStream fos = new FileOutputStream(ficheroCarpetas);
        fos.write(carpetasOcultas.getBytes());
        fos.close();
    }
}
