package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.vista.adaptadores.AdaptadorAnadirCancion;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCanciones;

public class AnadirCancionesActivity extends AppCompatActivity implements AdaptadorAnadirCancion.OnCancionClick, View.OnClickListener {

    public static final String EXTRA_CANCIONES_ANADIDAS = "extraCancionesAnadidas";

    private List<Cancion> todasCanciones;
    private List<Cancion> cancionesSeleccionadas;

    private Ajustes ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_anadir_canciones);
        cargarVista();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAtrasAnadirCancion)
            salir();
        else if (v.getId() == R.id.btnAceptarAnadirCanciones)
            anadirCanciones();
    }

    @Override
    public void onCancionClick(int indice) {
        Cancion cancion = todasCanciones.get(indice);

        if (cancionesSeleccionadas.contains(cancion))
            cancionesSeleccionadas.remove(cancion);
        else
            cancionesSeleccionadas.add(cancion);
    }

    private void salir() {
        setResult(RESULT_CANCELED);

        finish();
    }

    private void anadirCanciones() {
        String[] canciones = new String[cancionesSeleccionadas.size()];
        for (int i = 0; i < canciones.length; i++) {
            Cancion cancion = cancionesSeleccionadas.get(i);
            canciones[i] = cancion.getDatos();
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CANCIONES_ANADIDAS, canciones);
        setResult(RESULT_OK, intent);

        finish();
    }

    private void cargarVista() {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(this);
        ajustes = Ajustes.getInstance(this);

        todasCanciones = filtrarCanciones(accesoFichero.getTodasCanciones(), ajustes);

        cancionesSeleccionadas = new ArrayList<>();

        ImageView btnAtrasa = findViewById(R.id.btnAtrasAnadirCancion);
        btnAtrasa.setOnClickListener(this);

        ImageView btnAceptar = findViewById(R.id.btnAceptarAnadirCanciones);
        btnAceptar.setOnClickListener(this);

        cargarRecyclerView();
    }

    private void cargarRecyclerView() {
        Collections.sort(todasCanciones);
        IndexFastScrollRecyclerView recyclerView = findViewById(R.id.recyclerViewAnadirCanciones);
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

        if (todasCanciones.size() < 100)
            recyclerView.setIndexBarVisibility(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorAnadirCancion adaptador = new AdaptadorAnadirCancion(todasCanciones, this);
        recyclerView.setAdapter(adaptador);
        if (todasCanciones.size() == 0)
            recyclerView.setVisibility(View.INVISIBLE);
    }

    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.TemaClaro);
        }
    }
}