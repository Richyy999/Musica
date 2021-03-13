package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Carpeta;
import es.rbp.musica.vista.adaptadores.AdaptadorFiltrarCarpeta;

import static es.rbp.musica.modelo.AudioUtils.getCarpetas;

public class FiltroCarpetasActivity extends AppCompatActivity implements AdaptadorFiltrarCarpeta.OnCarpetaClick, View.OnClickListener {

    private static final String TAG = "Filtro Carpetas";

    private ImageView btnVolver, btnAceptar;

    private List<Cancion> canciones;

    private List<Carpeta> carpetas;

    private List<String> carpetasOcultas;

    private AdaptadorFiltrarCarpeta adaptador;

    private Ajustes ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_filtro_carpetas);
        ajustes = Ajustes.getInstance(this);
        cargarVista();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAceptar.getId())
            guardarAjustes();
        finish();
    }

    @Override
    public void onClick(int indice) {
        Carpeta carpeta = carpetas.get(indice);
        if (carpetasOcultas.contains(carpeta.getRuta()))
            carpetasOcultas.remove(carpeta.getRuta());
        else
            carpetasOcultas.add(carpeta.getRuta());
        adaptador.notifyDataSetChanged();
    }

    private void guardarAjustes() {
        ajustes.setCarpetasOcultas(carpetasOcultas);
        ajustes.guardarAjustes(this);
    }

    private void cargarAjustes() {
        Ajustes ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.TemaClaro);
        }
    }

    private void cargarVista() {
        AccesoFichero accesoFichero = AccesoFichero.getInstance(this);
        canciones = accesoFichero.getTodasCanciones();

        carpetasOcultas = new ArrayList<>();
        carpetasOcultas.addAll(ajustes.getCarpetasOcultas());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFiltrarCarpetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carpetas = getCarpetas(canciones);
        Collections.sort(carpetas);
        adaptador = new AdaptadorFiltrarCarpeta(carpetas, carpetasOcultas, this, this);
        recyclerView.setAdapter(adaptador);

        btnVolver = findViewById(R.id.btnVolverFiltroCarpetas);
        btnVolver.setOnClickListener(this);

        btnAceptar = findViewById(R.id.btnAceptarFiltroCarpetas);
        btnAceptar.setOnClickListener(this);
    }
}