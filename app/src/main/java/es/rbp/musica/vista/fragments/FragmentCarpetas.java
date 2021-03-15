package es.rbp.musica.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Carpeta;
import es.rbp.musica.vista.activities.MainActivity;
import es.rbp.musica.vista.adaptadores.AdaptadorCarpetas;

import static es.rbp.musica.modelo.AudioUtils.filtrarCanciones;
import static es.rbp.musica.modelo.AudioUtils.getCarpetas;

public class FragmentCarpetas extends Fragment implements AdaptadorCarpetas.OnCarpetaClicked {

    private Ajustes ajustes;

    private List<Carpeta> carpetas;

    public FragmentCarpetas() {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(getContext());
        ajustes = Ajustes.getInstance(getContext());
        List<Cancion> canciones = filtrarCanciones(accesoFichero.getTodasCanciones(), ajustes);
        carpetas = getCarpetas(canciones);
        Collections.sort(carpetas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_carpetas, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewCarpetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdaptadorCarpetas adaptador = new AdaptadorCarpetas(carpetas, this, getContext());
        recyclerView.setAdapter(adaptador);
        return root;
    }

    @Override
    public void onClick(int indice) {
        MainActivity mainActivity = (MainActivity) getActivity();
        Carpeta carpeta = carpetas.get(indice);
        mainActivity.cargarFavoritos(carpeta);
    }
}