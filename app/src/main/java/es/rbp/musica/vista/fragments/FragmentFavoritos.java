package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;

public class FragmentFavoritos extends Fragment {

    private List<Cancion> canciones;

    public FragmentFavoritos() {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(getContext());
        canciones = accesoFichero.getFavoritos();
    }

    public FragmentFavoritos(List<Cancion> canciones) {
        this.canciones = canciones;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdaptadorCanciones adaptador = new AdaptadorCanciones(canciones);
        recyclerView.setAdapter(adaptador);
        return root;
    }
}