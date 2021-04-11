package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.vista.activities.PlaylistActivity;

import static es.rbp.musica.modelo.AudioUtils.esFavorito;

public class SnackbarCancion implements SnackbarMusica, View.OnClickListener {

    public static final int ACCION_OCULTAR = -1;
    public static final int ACCION_ANADIR_A_LA_COLA = 0;
    public static final int ACCION_REPRODUCIR_SIGUIENTE = 1;
    public static final int ACCION_ANADIR_A_LA_PLAYLIST = 2;
    public static final int ACCION_ANADIR_A_FAVORITOS = 3;
    public static final int ACCION_ELIMINAR_DE_FAVORITOS = 4;
    public static final int ACCION_ELIMINAR_DE_LA_PLAYLIST = 5;

    private Snackbar snackbar;

    private View opacityPane;

    private Accion accion;

    public SnackbarCancion(Activity activity, View view, Accion accion, Cancion cancion, Ajustes ajustes) {
        this.accion = accion;

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_cancion, null);

        View snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        // Para evitar que se oculte el snacbar al pulsar sobre el contenedor
        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarCancion);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.INVISIBLE);

        LinearLayout btnAnadirAPlaylist = vistaPersonalizada.findViewById(R.id.btnAnadirAPlaylist);
        btnAnadirAPlaylist.setOnClickListener(this);

        LinearLayout btnAnadirACola = vistaPersonalizada.findViewById(R.id.btnAnadirACola);
        btnAnadirACola.setOnClickListener(this);

        LinearLayout btnReproducirSiguiente = vistaPersonalizada.findViewById(R.id.btnReproducirSiguiente);
        btnReproducirSiguiente.setOnClickListener(this);

        LinearLayout btnEliminarDeLaPlaylist = vistaPersonalizada.findViewById(R.id.btnEliminarDePlaylist);
        btnEliminarDeLaPlaylist.setOnClickListener(this);

        LinearLayout btnAnadirFavoritos = vistaPersonalizada.findViewById(R.id.btnAnadirAFavoritos);
        btnAnadirFavoritos.setOnClickListener(this);

        LinearLayout btnEliminarFavoritos = vistaPersonalizada.findViewById(R.id.btnEliminarFavoritos);
        btnEliminarFavoritos.setOnClickListener(this);

        if (!esFavorito(AccesoFichero.getInstance(activity), cancion))
            btnEliminarFavoritos.setVisibility(View.INVISIBLE);

        if (!(activity instanceof PlaylistActivity))
            btnEliminarDeLaPlaylist.setVisibility(View.GONE);

        TextView lblTitulo = vistaPersonalizada.findViewById(R.id.lblTituloSnackbarCancion);
        if (ajustes.isUtilizarNombreDeArchivo())
            lblTitulo.setText(cancion.getNombreArchivo());
        else
            lblTitulo.setText(cancion.getNombre());
        lblTitulo.setSelected(true);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAnadirAPlaylist:
                accion.realizarAccion(ACCION_ANADIR_A_LA_PLAYLIST);
                ocultar();
                break;
            case R.id.btnAnadirACola:
                accion.realizarAccion(ACCION_ANADIR_A_LA_COLA);
                ocultar();
                break;
            case R.id.btnReproducirSiguiente:
                accion.realizarAccion(ACCION_REPRODUCIR_SIGUIENTE);
                ocultar();
                break;
            case R.id.btnAnadirAFavoritos:
                accion.realizarAccion(ACCION_ANADIR_A_FAVORITOS);
                ocultar();
                break;
            case R.id.btnEliminarFavoritos:
                accion.realizarAccion(ACCION_ELIMINAR_DE_FAVORITOS);
                ocultar();
                break;
            case R.id.btnEliminarDePlaylist:
                accion.realizarAccion(ACCION_ELIMINAR_DE_LA_PLAYLIST);
                ocultar();
                break;
            case R.id.opacityPaneSnackbarCancion:
                accion.realizarAccion(ACCION_OCULTAR);
                ocultar();
                break;
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

    public interface Accion {
        void realizarAccion(int accion);
    }
}
