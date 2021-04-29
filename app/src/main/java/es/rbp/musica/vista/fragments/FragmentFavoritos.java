package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.activities.AnadirSnackbarMusica;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import es.rbp.musica.vista.snackbar.SnackbarCancion;
import es.rbp.musica.vista.snackbar.SnackbarMusica;
import es.rbp.musica.vista.snackbar.SnackbarPlaylists;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class FragmentFavoritos extends Fragment implements AdaptadorCanciones.OnCancionClick, SnackbarCancion.Accion, SnackbarPlaylists.Accion {

    private List<Cancion> canciones;

    private View root;

    private Ajustes ajustes;

    private Cancion cancionSeleccionada;

    private AccesoFichero accesoFichero;

    private boolean muestraFavoritos;

    public FragmentFavoritos() {
        accesoFichero = AccesoFichero.getInstance(getContext());
        this.canciones = accesoFichero.getFavoritos();
        this.muestraFavoritos = true;
    }

    public FragmentFavoritos(List<Cancion> canciones) {
        this.canciones = canciones;
        this.muestraFavoritos = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favoritos, container, false);
        ajustes = Ajustes.getInstance(getContext());
        accesoFichero = AccesoFichero.getInstance(getContext());
        actualizarRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (muestraFavoritos) {
            canciones = accesoFichero.getFavoritos();
            actualizarRecyclerView();
        }
    }

    @Override
    public void onMenuClicked(int indice) {
        cancionSeleccionada = canciones.get(indice);
        SnackbarMusica snackbarMusica = new SnackbarCancion(getActivity(), getActivity().findViewById(android.R.id.content),
                this, cancionSeleccionada, Ajustes.getInstance(getContext()));
        ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(snackbarMusica);
    }

    @Override
    public void onClick(int indice) {

    }

    @Override
    public void realizarAccion(int accion) {
        switch (accion) {
            case SnackbarCancion.ACCION_ANADIR_A_FAVORITOS:
                accesoFichero.anadirFavorito(cancionSeleccionada.getDatos());
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(null);
                break;
            case SnackbarCancion.ACCION_ELIMINAR_DE_FAVORITOS:
                accesoFichero.eliminarFavorito(cancionSeleccionada.getDatos());
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(null);
                break;
            case SnackbarCancion.ACCION_OCULTAR:
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(null);
                break;
            case SnackbarCancion.ACCION_ANADIR_A_LA_PLAYLIST:
                Log.d("FragmentPLaylists", "añadir playlist");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostrarPlaylists();
                    }
                }, 400);
                break;
        }

        if (muestraFavoritos) {
            canciones = accesoFichero.getFavoritos();
            actualizarRecyclerView();
        }
    }

    @Override
    public void onPlaylistSeleccionada(int indicePlaylist) {
        if (indicePlaylist != SnackbarPlaylists.NINGUNA_PLAYLIST) {
            Playlist playlist = accesoFichero.getPlaylistPorIndice(indicePlaylist);
            playlist.getCanciones().add(cancionSeleccionada.getDatos());
            accesoFichero.guardarPlaylist(playlist);
        }

        ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(null);
    }

    private void mostrarPlaylists() {
        List<Playlist> playlists = accesoFichero.getPlaylists();
        SnackbarMusica snackbarMusica = new SnackbarPlaylists(getActivity(), getActivity().findViewById(android.R.id.content), this, playlists);
        ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(snackbarMusica);
    }

    private void actualizarRecyclerView() {
        Collections.sort(canciones);
        IndexFastScrollRecyclerView recyclerView = root.findViewById(R.id.recyclerViewFavoritos);
        if (ajustes.isModoOscuro()) {
            recyclerView.setIndexBarTextColor(R.color.subtituloOscuro);
            recyclerView.setIndexBarColor(R.color.fondoAppOscuro);
        } else {
            recyclerView.setIndexBarTextColor(R.color.subtituloClaro);
            recyclerView.setIndexBarColor(R.color.fondoAppClaro);
        }
        recyclerView.setPreviewTransparentValue((float) 0.5);
        recyclerView.setIndexTextSize(10);
        recyclerView.setIndexBarStrokeVisibility(false);

        if (canciones.size() < 100)
            recyclerView.setIndexBarVisibility(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdaptadorCanciones adaptador = new AdaptadorCanciones(canciones, this);
        recyclerView.setAdapter(adaptador);
        if (canciones.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);
    }
}