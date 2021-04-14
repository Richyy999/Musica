package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.adaptadores.AdaptadorAnadirCancion;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCanciones;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorQuery;
import static es.rbp.musica.modelo.AudioUtils.seleccionarCanciones;

public class SeleccionaCancionesActivity extends AppCompatActivity implements AdaptadorAnadirCancion.OnCancionClick, View.OnClickListener, TextWatcher {

    public static final String EXTRA_CANCIONES_ANADIDAS = "extraCancionesAnadidas";
    public static final String EXTRA_MODO_SELECCION = "modoDeSeleccion";

    public static final String ACCION_ANADIR = "accionAnadir";
    public static final String ACCION_ELIMINAR = "accionEliminar";

    public static final int NINGUNA_PLAYLIST = -1;

    private static final String TAG = "AÑADIR CANCIÓN";

    private List<Cancion> todasCancionesFiltradas;
    private List<Cancion> cancionesFiltradas;
    private List<Cancion> cancionesSeleccionadas;

    private AdaptadorAnadirCancion adaptador;

    private Ajustes ajustes;

    private String accion;

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
        else if (v.getId() == R.id.btnAceptarAnadirCanciones && accion.equals(ACCION_ANADIR))
            enviarCanciones();
        else if (v.getId() == R.id.btnAceptarAnadirCanciones && accion.equals(ACCION_ELIMINAR))
            enviarCanciones();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String query = s.toString().trim();
        cancionesFiltradas = filtrarCancionesPorQuery(todasCancionesFiltradas, query, ajustes);
        cargarRecyclerView();
    }

    @Override
    public void onCancionClick(int indice) {
        Cancion cancion = cancionesFiltradas.get(indice);

        if (cancionesSeleccionadas.contains(cancion))
            cancionesSeleccionadas.remove(cancion);
        else
            cancionesSeleccionadas.add(cancion);

        cancion.setSeleccionada(cancionesSeleccionadas.contains(cancion));

        Log.d(TAG, "Cancion Pulsada: " + cancion.getDatos() + " Seleccionada: " + cancion.isSeleccionada());

        adaptador.notifyDataSetChanged();
    }

    private void salir() {
        setResult(RESULT_CANCELED);

        seleccionarCanciones(todasCancionesFiltradas, false);

        finish();
    }

    private void enviarCanciones() {
        String[] canciones = new String[cancionesSeleccionadas.size()];
        for (int i = 0; i < canciones.length; i++) {
            Cancion cancion = cancionesSeleccionadas.get(i);
            canciones[i] = cancion.getDatos();
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CANCIONES_ANADIDAS, canciones);
        intent.setAction(accion);
        setResult(RESULT_OK, intent);

        seleccionarCanciones(todasCancionesFiltradas, false);

        finish();
    }

    private void cargarVista() {
        accion = getIntent().getStringExtra(EXTRA_MODO_SELECCION);
        if (accion == null)
            salir();

        AccesoFichero accesoFichero = AccesoFichero.getInstance(this);
        ajustes = Ajustes.getInstance(this);

        if (accion.equals(ACCION_ANADIR))
            todasCancionesFiltradas = filtrarCanciones(accesoFichero.getTodasCanciones(), ajustes);
        else if (accion.equals(ACCION_ELIMINAR)) {
            int indicePlaylist = getIntent().getIntExtra(Playlist.EXTRA_PLAYLIST, NINGUNA_PLAYLIST);
            if (indicePlaylist == NINGUNA_PLAYLIST)
                salir();

            Playlist playlist = accesoFichero.getPlaylistPorIndice(indicePlaylist);
            todasCancionesFiltradas = filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), playlist.getCanciones());
        }

        cancionesFiltradas = filtrarCanciones(todasCancionesFiltradas, ajustes);

        cancionesSeleccionadas = new ArrayList<>();

        ImageView btnAtrasa = findViewById(R.id.btnAtrasAnadirCancion);
        btnAtrasa.setOnClickListener(this);

        ImageView btnAceptar = findViewById(R.id.btnAceptarAnadirCanciones);
        btnAceptar.setOnClickListener(this);

        EditText txtFiltrar = findViewById(R.id.txtFiltrarAnadirCancion);
        txtFiltrar.addTextChangedListener(this);

        cargarRecyclerView();
    }

    private void cargarRecyclerView() {
        Collections.sort(cancionesFiltradas);
        IndexFastScrollRecyclerView recyclerView = findViewById(R.id.recyclerViewAnadirCanciones);
        recyclerView.setHasFixedSize(false);
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

        if (cancionesFiltradas.size() < 100)
            recyclerView.setIndexBarVisibility(false);
        else
            recyclerView.setIndexBarVisibility(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorAnadirCancion(cancionesFiltradas, this);
        recyclerView.setAdapter(adaptador);
    }

    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            setTheme(R.style.TemaClaro);
        }
    }
}