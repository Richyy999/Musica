package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.vista.adaptadores.AdaptadorCola;

public class SnackbarCola implements SnackbarMusica, View.OnClickListener, AdaptadorCola.OnCancionColaClick {

    private Snackbar snackbar;

    private View opacityPane;

    public SnackbarCola(Activity activity, View view, Ajustes ajustes, Cola cola) {

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

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

        ImageView btnBucle = vistaPersonalizada.findViewById(R.id.btnBucleSnackbarCola);
        btnBucle.setOnClickListener(this);

        ImageView btnAleatorio = vistaPersonalizada.findViewById(R.id.btnAleatorioSnackbarCola);
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
                ocultar();
                break;
        }
    }

    @Override
    public void eliminarDeCola(int indice) {

    }

    private void mostrarOpacityPane() {
        AlphaAnimation animacion = new AlphaAnimation(0, 255);
        animacion.setDuration(200);
        this.opacityPane.setAlpha(0);
        this.opacityPane.setVisibility(View.VISIBLE);
        this.opacityPane.startAnimation(animacion);
        this.opacityPane.setAlpha(1);
    }
}
