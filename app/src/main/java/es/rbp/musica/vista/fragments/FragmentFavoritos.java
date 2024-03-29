package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.AudioUtils;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.activities.AnadirSnackbarMusica;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import es.rbp.musica.vista.snackbar.SnackbarCancion;
import es.rbp.musica.vista.snackbar.SnackbarMusica;
import es.rbp.musica.vista.snackbar.SnackbarPlaylists;
import es.rbp.musica.vista.snackbar.SnackbarTexto;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class FragmentFavoritos extends Fragment implements AdaptadorCanciones.OnCancionClick, SnackbarCancion.Accion, SnackbarPlaylists.Accion, SnackbarTexto.Accion {

    private static final String TAG = "FRAGMENT_FAVORITOS";

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
                ((AnadirSnackbarMusica) getActivity()).cerrar();
                break;
            case SnackbarCancion.ACCION_ELIMINAR_DE_FAVORITOS:
                accesoFichero.eliminarFavorito(cancionSeleccionada.getDatos());
                ((AnadirSnackbarMusica) getActivity()).cerrar();
                break;
            case SnackbarCancion.ACCION_OCULTAR:
                ((AnadirSnackbarMusica) getActivity()).cerrar();
                break;
            case SnackbarCancion.ACCION_ANADIR_A_LA_PLAYLIST:
                List<Playlist> playlists = accesoFichero.getPlaylists();
                if (playlists.size() == 0)
                    crearPlaylist();
                else
                    new Handler().postDelayed(() -> mostrarPlaylists(), 400);
                break;
            case SnackbarCancion.ACCION_ANADIR_A_LA_COLA:
                Cola cola = accesoFichero.getCola();
                cola.anadirALaCola(cancionSeleccionada);
                accesoFichero.guardarCola(cola);
                ((AnadirSnackbarMusica) getActivity()).cerrar();
                break;
            case SnackbarCancion.ACCION_REPRODUCIR_SIGUIENTE:
                Cola cola1 = accesoFichero.getCola();
                cola1.reproducirSiguiente(cancionSeleccionada);
                accesoFichero.guardarCola(cola1);
                ((AnadirSnackbarMusica) getActivity()).cerrar();
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

        ((AnadirSnackbarMusica) getActivity()).cerrar();
    }

    @Override
    public void realizarAccion(String texto) {
        if (texto == null)
            return;

        try {
            accesoFichero.crearPlaylist(texto, ajustes);
            Playlist playlist = accesoFichero.getPlaylists().get(0);
            playlist.getCanciones().add(cancionSeleccionada.getDatos());
            accesoFichero.guardarPlaylist(playlist);
        } catch (IOException e) {
            Log.e(TAG, "Exception", e);
        }
    }

    private void crearPlaylist() {
        ((AnadirSnackbarMusica) getActivity()).cerrar();
        new SnackbarTexto(this, getActivity(), R.string.nombreDeLaPlaylist, R.string.introduceNombrePlaylist).show();
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
        recyclerView.setPreviewTransparentValue(0.5F);
        recyclerView.setIndexTextSize(10);
        recyclerView.setIndexBarStrokeVisibility(false);

        if (canciones.size() < 100)
            recyclerView.setIndexBarVisibility(false);
        else
            recyclerView.setIndexBarVisibility(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdaptadorCanciones adaptador = new AdaptadorCanciones(canciones, this);
        recyclerView.setAdapter(adaptador);
        if (canciones.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);
        else
            recyclerView.setVisibility(View.VISIBLE);
    }
}