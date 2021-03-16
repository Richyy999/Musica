package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

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
        Ajustes ajustes = Ajustes.getInstance(getContext());
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
        AdaptadorCanciones adaptador = new AdaptadorCanciones(canciones);
        recyclerView.setAdapter(adaptador);
        if (canciones.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);
        return root;
    }
}