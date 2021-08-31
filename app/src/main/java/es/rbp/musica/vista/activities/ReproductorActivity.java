package es.rbp.musica.vista.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.vista.snackbar.SnackbarCola;
import es.rbp.musica.vista.snackbar.SnackbarMusica;

import static es.rbp.musica.modelo.AudioUtils.esFavorito;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;
import static es.rbp.musica.modelo.AudioUtils.showToast;
import static es.rbp.musica.vista.activities.SeleccionaCancionesActivity.EXTRA_CANCIONES_ANADIDAS;

public class ReproductorActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, SnackbarCola.Accion {

    private static final String TAG = "REPRODUCTOR ACTIVITY";

    private SnackbarMusica snackbarMusica;

    private RoundedImageView imgCancion;

    private TextView lblNombreCancion;
    private TextView lblProgresoActual;
    private TextView lblProgresoTotal;

    private ImageView btnBucle;
    private ImageView btnAleatorio;
    private ImageView btnFavoritos;

    private AppCompatSeekBar seekBar;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    private Cola cola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getWindow().setDecorFitsSystemWindows(false);

        getWindow().setStatusBarColor(getColor(android.R.color.transparent));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_reproductor);
        cola = AccesoFichero.getInstance(this).getCola();
        accesoFichero = AccesoFichero.getInstance(this);
        cargarVista();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cancion cancion = cola.getCancionActual();
        actualizarBotones(cancion);
        actualizarProgreso(cola.getProgresoActual());
        actualizarCancion(cancion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            snackbarMusica.ocultar();
            String accion = data.getAction();
            String[] nombreCanciones = data.getStringArrayExtra(EXTRA_CANCIONES_ANADIDAS);
            if (accion.equals(SeleccionaCancionesActivity.ACCION_ANADIR)) {
                List<String> listaNombreCanciones = new ArrayList<>();
                for (int i = 0; i < nombreCanciones.length; i++) {
                    Log.i(TAG, "Añadido: " + nombreCanciones[i]);
                    listaNombreCanciones.add(nombreCanciones[i]);
                }
                cola.anadirALaCola(filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), listaNombreCanciones));
            } else {
                Log.i(TAG, "No se han añadido canciones");
            }
            snackbarMusica = new SnackbarCola(this, findViewById(android.R.id.content), ajustes, cola, this);
        }
    }

    @Override
    public void onBackPressed() {
        if (snackbarMusica != null) {
            snackbarMusica.ocultar();
            snackbarMusica = null;
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAtrasReproductor:
                finish();
                break;
            case R.id.btnCancionAnteriorActivity:
                Cancion cancionAnterior = cola.cancionAnterior();
                if (cancionAnterior != null) {
                    actualizarBotones(cancionAnterior);
                    actualizarCancion(cancionAnterior);
                }
                break;
            case R.id.btnSiguienteCancionActivity:
                Cancion siguienteCancion = cola.siguienteCancion();
                if (siguienteCancion != null) {
                    actualizarBotones(siguienteCancion);
                    actualizarCancion(siguienteCancion);
                }
                break;
            case R.id.btnColaReproductor:
                snackbarMusica = new SnackbarCola(this, findViewById(android.R.id.content), ajustes, cola, this);
                break;
            case R.id.btnBucle:
                cambiarModoRepeticion();
                break;
            case R.id.btnAleatorio:
                cambiarModoReproduccion();
                break;
            case R.id.btnFavoritoReproductor:
                if (esFavorito(accesoFichero, cola.getCancionActual()))
                    accesoFichero.eliminarFavorito(cola.getCancionActual().getDatos());
                else
                    accesoFichero.anadirFavorito(cola.getCancionActual().getDatos());

                actualizarBotones(cola.getCancionActual());
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            actualizarProgreso(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void realizarAccionCola(int accion) {
        switch (accion) {
            case SnackbarCola.ACCION_OCULTAR:
                snackbarMusica.ocultar();
                snackbarMusica = null;
                break;
            case SnackbarCola.ACCION_ELIMINAR_COLA:
                cola.eliminarCola();
                accesoFichero.guardarCola(cola);
                finish();
                break;
            case SnackbarCola.ACCION_ANADIR_CANCIONES:
                Intent intent = new Intent(this, SeleccionaCancionesActivity.class);
                intent.putExtra(SeleccionaCancionesActivity.EXTRA_MODO_SELECCION, SeleccionaCancionesActivity.ACCION_ANADIR);
                startActivityForResult(intent, SnackbarCola.CODIGO_REQUEST_SNACKBAR_COLA);
                break;
        }
        if (accion != SnackbarCola.ACCION_ELIMINAR_COLA) {
            actualizarBotones(cola.getCancionActual());
            actualizarProgreso(cola.getProgresoActual());
        }
    }

    /**
     * Cambia el odo de reproducción de la {@link Cola}. Si el modo actual es {@link Cola#REPRODUCCION_LINEAL} cambia a
     * {@link Cola#REPRODUCCION_ALEATORIA} y viceversa
     */
    private void cambiarModoReproduccion() {
        if (cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA) {
            cola.cambiarModoReproduccion(Cola.REPRODUCCION_LINEAL);
            showToast(this, R.string.reproduccionLinear);
        } else if (cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL) {
            cola.cambiarModoReproduccion(Cola.REPRODUCCION_ALEATORIA);
            showToast(this, R.string.reproduccionAleatoria);
        }
        accesoFichero.guardarCola(cola);
        actualizarBotones(cola.getCancionActual());
    }

    /**
     * Cambia el modo de repetición de la {@link Cola}. Si el modo actual es {@link Cola#REPETICION_EN_BUCLE} cambia a
     * {@link Cola#REPETICION_UNA_VEZ} y viceversa
     */
    private void cambiarModoRepeticion() {
        if (cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE) {
            cola.cambiarModoRepeticion(Cola.REPETICION_UNA_VEZ);
            showToast(this, R.string.noRepetir);
        } else if (cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ) {
            cola.cambiarModoRepeticion(Cola.REPETICION_EN_BUCLE);
            showToast(this, R.string.repetirEnBucle);
        }
        accesoFichero.guardarCola(cola);
        actualizarBotones(cola.getCancionActual());
    }

    /**
     * Actualiza la vista según el progreso de la canción que se está reproduciendo
     *
     * @param progreso progreso de la canción que se está reproduciendo
     * @see Cola#getProgresoActual()
     */
    private void actualizarProgreso(int progreso) {
        seekBar.setProgress(progreso);

        String minutos = String.valueOf(progreso / 60);
        if (minutos.length() == 1)
            minutos = "0" + minutos;
        String segundos = String.valueOf(progreso % 60);
        if (segundos.length() == 1)
            segundos = "0" + segundos;
        String tiempoActual = minutos + ":" + segundos;
        lblProgresoActual.setText(tiempoActual);
    }

    /**
     * Actualiza el título y la imagen según la canción actual
     *
     * @param cancion canción que se está reproduciendo
     * @see Cola#getCancionActual()
     */
    private void actualizarCancion(Cancion cancion) {
        Bitmap imagenCancion = cancion.getImagenAlbum(this);
        Glide.with(this).load(imagenCancion).into(imgCancion);

        if (ajustes.isUtilizarNombreDeArchivo())
            lblNombreCancion.setText(cancion.getNombreArchivo());
        else
            lblNombreCancion.setText(cancion.getNombre());
    }

    /**
     * Actualiza los datos de los botones según la canción actual
     *
     * @param cancion {@link Cancion} que se está reproduciendo
     * @see Cola#getCancionActual()
     */
    private void actualizarBotones(Cancion cancion) {
        Log.i(TAG, "Duración: " + cancion.getDuracion());
        String minutos = String.valueOf(cancion.getDuracion() / 60);
        if (minutos.length() == 1)
            minutos = "0" + minutos;
        String segundos = String.valueOf(cancion.getDuracion() % 60);
        if (segundos.length() == 1)
            segundos = "0" + segundos;
        String tiempoTotal = minutos + ":" + segundos;
        lblProgresoTotal.setText(tiempoTotal);

        // Actualiza el icono de bucle en función del tema y del modo de repetición de la cola
        if (ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_bucle_activado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_bucle_activado_claro));
        else if (ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_bucle_desactivado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_bucle_desactivado_claro));

        // Actualiza el icono de aleatorio en función del tema y del modo de reproducción de la cola
        if (ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_aleatorio_desactivado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_aleatorio_desactivado_claro));
        else if (ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_aleatorio_activado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_aleatorio_activado_claro));

        // Actualiza el icono de favoritos en función del tema y de si la canción está en favoritos o no
        if (ajustes.isModoOscuro() && esFavorito(accesoFichero, cancion))
            btnFavoritos.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_favoritos_lleno_oscuro));
        else if (!ajustes.isModoOscuro() && esFavorito(accesoFichero, cancion))
            btnFavoritos.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_favoritos_lleno_claro));
        else if (ajustes.isModoOscuro() && !esFavorito(accesoFichero, cancion))
            btnFavoritos.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_favorito_vacio_oscuro));
        else if (!ajustes.isModoOscuro() && !esFavorito(accesoFichero, cancion))
            btnFavoritos.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icono_favorito_vacio_claro));

        seekBar.setMax(cancion.getDuracion());
    }

    /**
     * Instancia los {@link Ajustes} de la aplicación y actualiza el tema de la interfaz
     */
    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.AnimacionAbajoArribaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController wic = getWindow().getDecorView().getWindowInsetsController();
                wic.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            } else
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AnimacionAbajoArribaClaro);
        }
    }

    /**
     * Inicializa la vista y asigna los listeners
     */
    private void cargarVista() {
        ImageView btnAtras = findViewById(R.id.btnAtrasReproductor);
        btnAtras.setOnClickListener(this);

        imgCancion = findViewById(R.id.imgCancion);

        lblNombreCancion = findViewById(R.id.lblNombreCancionReproductor);
        lblNombreCancion.setSelected(true);

        lblProgresoActual = findViewById(R.id.lblTiempoActual);
        lblProgresoTotal = findViewById(R.id.lblTiempoTotal);

        ImageView btnCancionAnterior = findViewById(R.id.btnCancionAnteriorActivity);
        btnCancionAnterior.setOnClickListener(this);

        ImageView btnSiguienteCancion = findViewById(R.id.btnSiguienteCancionActivity);
        btnSiguienteCancion.setOnClickListener(this);

        ImageView btnCola = findViewById(R.id.btnColaReproductor);
        btnCola.setOnClickListener(this);

        btnBucle = findViewById(R.id.btnBucle);
        btnBucle.setOnClickListener(this);

        btnAleatorio = findViewById(R.id.btnAleatorio);
        btnAleatorio.setOnClickListener(this);

        btnFavoritos = findViewById(R.id.btnFavoritoReproductor);
        btnFavoritos.setOnClickListener(this);

        seekBar = findViewById(R.id.seekbarReproductor);
        seekBar.setOnSeekBarChangeListener(this);

        actualizarBotones(cola.getCancionActual());
        actualizarCancion(cola.getCancionActual());
    }
}