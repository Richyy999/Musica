package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.entidad.Playlist;

public class FragmentPlaylist extends Fragment {

    private List<Playlist> playlists;

    public FragmentPlaylist() {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(getContext());
        playlists = accesoFichero.getPlaylists();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewPlaylist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return v;
    }
}