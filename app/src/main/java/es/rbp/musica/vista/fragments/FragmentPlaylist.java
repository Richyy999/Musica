package es.rbp.musica.vista.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.activities.PlaylistActivity;
import es.rbp.musica.vista.adaptadores.AdaptadorPlaylists;
import es.rbp.musica.vista.snackbar.SnackbarTexto;

import static es.rbp.musica.modelo.entidad.Playlist.EXTRA_PLAYLIST;

public class FragmentPlaylist extends Fragment implements SnackbarTexto.Accion, AdaptadorPlaylists.OnPlaylistClick, View.OnClickListener {

    public static final String FRAGMENT_TAG = "FRAGMENT_PLAYLISTS_TAG";

    private static final String TAG = "FRAGMENT_PLAYLISTS";

    private List<Playlist> playlists;

    private AdaptadorPlaylists adaptador;

    private RecyclerView recyclerView;

    private final AccesoFichero accesoFichero;

    public FragmentPlaylist() {
        accesoFichero = AccesoFichero.getInstance(getContext());
        playlists = accesoFichero.getPlaylists();

        if (playlists == null)
            playlists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);
        Collections.sort(playlists);
        recyclerView = root.findViewById(R.id.recyclerViewPlaylist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adaptador = new AdaptadorPlaylists(playlists, this, getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adaptador);

        if (playlists.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);

        ImageView btnAnadirPlailist = root.findViewById(R.id.btnCrearPlaylist);
        btnAnadirPlailist.setOnClickListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCrearPlaylist)
            new SnackbarTexto(this, getActivity(), R.string.nombreDeLaPlaylist, R.string.introduceNombrePlaylist).show();
    }

    @Override
    public void realizarAccion(String texto) {
        try {
            if (texto != null) {
                Playlist nuevaPlaylist = accesoFichero.crearPlaylist(texto, Ajustes.getInstance(getContext()));
                accesoFichero.guardarPlaylist(nuevaPlaylist);

                adaptador.notifyItemInserted(playlists.size() - 1);
                adaptador.notifyDataSetChanged();

                recyclerView.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onPlaylistClick(int indice, AdaptadorPlaylists.MyHolder holder) {
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
        intent.putExtra(EXTRA_PLAYLIST, indice);
        Pair<View, String> parImagen = Pair.create((View) holder.imgPlaylist, ViewCompat.getTransitionName(holder.imgPlaylist));
        Pair<View, String> parNombre = Pair.create((View) holder.lblNombre, ViewCompat.getTransitionName(holder.lblNombre));
        Pair<View, String> parNumCanciones = Pair.create((View) holder.lblNumCanciones, ViewCompat.getTransitionName(holder.lblNumCanciones));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), parImagen, parNombre, parNumCanciones);
        startActivity(intent, options.toBundle());
    }
}