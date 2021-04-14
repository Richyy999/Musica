package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;

public class SnackbarMenuPlaylist implements SnackbarMusica, View.OnClickListener {

    public static final int ACCION_OCULTAR = 0;
    public static final int ACCION_ELIMINAR_PLAYLIST = 1;
    public static final int ACCION_REPRODUCIR_SIGUIENTE = 2;

    private Snackbar snackbar;

    private View opacityPane;

    private Accion accion;

    public SnackbarMenuPlaylist(Activity activity, View view, Accion accion) {
        this.accion = accion;

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_menu_playlist, null);

        View snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        // Para evitar que se oculte el snacbar al pulsar sobre el contenedor
        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarMenuPlaylist);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.INVISIBLE);

        LinearLayout btnEliminarPlaylist = vistaPersonalizada.findViewById(R.id.btnEliminarPlaylist);
        btnEliminarPlaylist.setOnClickListener(this);

        LinearLayout btnReproducirSiguiente = vistaPersonalizada.findViewById(R.id.btnReproducirSiguientePlaylist);
        btnReproducirSiguiente.setOnClickListener(this);

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
            case R.id.btnEliminarPlaylist:
                accion.realizarAccionPlaylist(ACCION_ELIMINAR_PLAYLIST);
                ocultar();
                break;
            case R.id.btnReproducirSiguientePlaylist:
                accion.realizarAccionPlaylist(ACCION_REPRODUCIR_SIGUIENTE);
                ocultar();
                break;
            case R.id.opacityPaneSnackbarMenuPlaylist:
                accion.realizarAccionPlaylist(ACCION_OCULTAR);
                ocultar();
        }
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
        void realizarAccionPlaylist(int accion);
    }
}
