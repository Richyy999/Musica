package es.rbp.musica.vista.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.activities.PlaylistActivity;
import es.rbp.musica.vista.adaptadores.AdaptadorPlaylists;
import es.rbp.musica.vista.snackbar.SnackbarCrearPlaylist;

import static es.rbp.musica.modelo.entidad.Playlist.EXTRA_PLAYLIST;

public class FragmentPlaylist extends Fragment implements SnackbarCrearPlaylist.Accion, AdaptadorPlaylists.OnPlaylistClick, View.OnClickListener {

    private List<Playlist> playlists;

    private AdaptadorPlaylists adaptador;

    private AccesoFichero accesoFichero;

    private View root;

    public FragmentPlaylist() {
        accesoFichero = AccesoFichero.getInstance(getContext());
        playlists = accesoFichero.getPlaylists();

        if (playlists == null)
            playlists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_playlist, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPlaylist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adaptador = new AdaptadorPlaylists(playlists, this, getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adaptador);

        if (playlists.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);

        ImageView btnAnadirPlailist = root.findViewById(R.id.btnCrearPlaylist);
        btnAnadirPlailist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCrearPlaylist)
            new SnackbarCrearPlaylist(this, getActivity()).show();
    }

    @Override
    public void crearPlaylist(String nombrePlaylist) {
        Playlist nuevaPlaylist = new Playlist(nombrePlaylist);
        playlists.add(nuevaPlaylist);
        accesoFichero.guardarPlaylists(playlists);

        adaptador.notifyItemInserted(playlists.size() - 1);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onPlaylistClick(int indice) {
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
        intent.putExtra(EXTRA_PLAYLIST, indice);
        startActivity(intent);
    }
}