package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.vista.adaptadores.AdaptadorCola;

public class SnackbarCola implements SnackbarMusica, View.OnClickListener, AdaptadorCola.OnCancionColaClick {

    public static final int CODIGO_REQUEST_SNACKBAR_COLA = 20;

    public static final int ACCION_OCULTAR = 0;
    public static final int ACCION_GUARDAR_COLA = 1;
    public static final int ACCION_ELIMINAR_COLA = 2;
    public static final int ACCION_ANADIR_CANCIONES = 3;

    private Accion accion;

    private Snackbar snackbar;

    private View opacityPane;

    private ImageView btnBucle;
    private ImageView btnAleatorio;

    private Activity activity;

    private Ajustes ajustes;

    private Cola cola;

    public SnackbarCola(Activity activity, View view, Ajustes ajustes, Cola cola, Accion accion) {
        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        this.accion = accion;
        this.cola = cola;
        this.activity = activity;
        this.ajustes = Ajustes.getInstance(activity);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_cola, null);

        View snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        // Para evitar que se oculte el snacbar al pulsar sobre el contenedor
        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarCola);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.INVISIBLE);

        btnBucle = vistaPersonalizada.findViewById(R.id.btnBucleSnackbarCola);
        btnBucle.setOnClickListener(this);

        btnAleatorio = vistaPersonalizada.findViewById(R.id.btnAleatorioSnackbarCola);
        btnAleatorio.setOnClickListener(this);

        ImageView btnEliminar = vistaPersonalizada.findViewById(R.id.btnEliminarSnackbarCola);
        btnEliminar.setOnClickListener(this);

        ImageView btnGuardar = vistaPersonalizada.findViewById(R.id.btnGuardarSnackbarCola);
        btnGuardar.setOnClickListener(this);

        ImageView btnAnadirCanciones = vistaPersonalizada.findViewById(R.id.btnAnadirCancionesSnackbarCola);
        btnAnadirCanciones.setOnClickListener(this);

        TextView btnCerrar = vistaPersonalizada.findViewById(R.id.btnCerrarSnackbarCola);
        btnCerrar.setOnClickListener(this);

        RecyclerView recyclerView = vistaPersonalizada.findViewById(R.id.recyclerViewSnackbarCola);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        AdaptadorCola adaptador = new AdaptadorCola(cola.getListaCanciones(), this, ajustes, cola, activity);
        recyclerView.setAdapter(adaptador);

        layout.addView(vistaPersonalizada);
        layout.setPadding(0, 0, 0, 0);

        this.snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarOpacityPane();
            }
        }, 300);
        actualizarBotones();
    }

    @Override
    public void ocultar() {
        AlphaAnimation animacion = new AlphaAnimation(255, 0);
        animacion.setDuration(300);
        this.opacityPane.startAnimation(animacion);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                opacityPane.setVisibility(View.GONE);
                snackbar.dismiss();
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCerrarSnackbarCola:
            case R.id.opacityPaneSnackbarCola:
                accion.realizarAccionCola(ACCION_OCULTAR);
                ocultar();
                break;
            case R.id.btnBucleSnackbarCola:
                cambiarModoRepeticion();
                break;
            case R.id.btnAleatorioSnackbarCola:
                cambiarModoReproduccion();
                break;
            case R.id.btnEliminarSnackbarCola:
                accion.realizarAccionCola(ACCION_ELIMINAR_COLA);
                ocultar();
                break;
            case R.id.btnGuardarSnackbarCola:
                accion.realizarAccionCola(ACCION_GUARDAR_COLA);
                ocultar();
                break;
            case R.id.btnAnadirCancionesSnackbarCola:
                accion.realizarAccionCola(ACCION_ANADIR_CANCIONES);
                ocultar();
                break;
        }
    }

    @Override
    public void eliminarDeCola(int indice) {

    }

    /**
     * Actualiza el aspecto de los botones {@link SnackbarCola#btnBucle} y {@link SnackbarCola#btnAleatorio} en función del tema y de la configuración de la {@link Cola}
     */
    private void actualizarBotones() {
        // Actualiza el icono de bucle en función del tema y del modo de repetición de la cola
        if (ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_bucle_activado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_bucle_activado_claro));
        else if (ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_bucle_desactivado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ)
            btnBucle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_bucle_desactivado_claro));

        // Actualiza el icono de aleatorio en función del tema y del modo de reproducción de la cola
        if (ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_aleatorio_desactivado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_aleatorio_desactivado_claro));
        else if (ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_aleatorio_activado_oscuro));
        else if (!ajustes.isModoOscuro() && cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA)
            btnAleatorio.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icono_aleatorio_activado_claro));
    }

    /**
     * Cambia el modo de repetición de la {@link Cola}. Si el modo actual es {@link Cola#REPETICION_EN_BUCLE} cambia a {@link Cola#REPETICION_UNA_VEZ} y viceversa
     */
    private void cambiarModoRepeticion() {
        if (cola.getModoRepeticion() == Cola.REPETICION_EN_BUCLE)
            cola.cambiarModoRepeticion(Cola.REPETICION_UNA_VEZ);
        else if (cola.getModoRepeticion() == Cola.REPETICION_UNA_VEZ)
            cola.cambiarModoRepeticion(Cola.REPETICION_EN_BUCLE);

        actualizarBotones();
    }

    /**
     * Cambia el odo de reproducción de la {@link Cola}. Si el modo actual es {@link Cola#REPRODUCCION_LINEAL} cambia a {@link Cola#REPRODUCCION_ALEATORIA} y viceversa
     */
    private void cambiarModoReproduccion() {
        if (cola.getModoReproduccion() == Cola.REPRODUCCION_ALEATORIA)
            cola.cambiarModoReproduccion(Cola.REPRODUCCION_LINEAL);
        else if (cola.getModoReproduccion() == Cola.REPRODUCCION_LINEAL)
            cola.cambiarModoReproduccion(Cola.REPRODUCCION_ALEATORIA);

        actualizarBotones();
    }

    private void mostrarOpacityPane() {
        AlphaAnimation animacion = new AlphaAnimation(0, 255);
        animacion.setDuration(200);
        this.opacityPane.setAlpha(0);
        this.opacityPane.setVisibility(View.VISIBLE);
        this.opacityPane.startAnimation(animacion);
        this.opacityPane.setAlpha(1);
    }

    public interface Accion {
        void realizarAccionCola(int accion);
    }
}
