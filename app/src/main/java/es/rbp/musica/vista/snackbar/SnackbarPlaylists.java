package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.adaptadores.AdaptadorSnackbarPlaylist;

public class SnackbarPlaylists implements SnackbarMusica, View.OnClickListener, AdaptadorSnackbarPlaylist.OnPlaylistClicked {

    public static final int NINGUNA_PLAYLIST = -1;

    private Snackbar snackbar;

    private View opacityPane;

    private Accion accion;

    public SnackbarPlaylists(Activity activity, View view, Accion accion, List<Playlist> playlists) {
        this.accion = accion;

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_playlists, null);

        View snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        // Para evitar que se oculte el snacbar al pulsar sobre el contenedor
        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarPlaylist);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = vistaPersonalizada.findViewById(R.id.recyclerViewSnackbarPlaylists);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(false);

        AdaptadorSnackbarPlaylist adaptador = new AdaptadorSnackbarPlaylist(playlists, this);
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
    public void onClick(View v) {
        if (v.getId() == opacityPane.getId()) {
            accion.onPlaylistSeleccionada(NINGUNA_PLAYLIST);
            ocultar();
        }
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
    public void onPlaylistClick(int indice) {
        accion.onPlaylistSeleccionada(indice);
        ocultar();
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
        void onPlaylistSeleccionada(int indicePlaylist);
    }
}
