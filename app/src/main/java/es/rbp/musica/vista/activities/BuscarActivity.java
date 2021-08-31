package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import es.rbp.musica.vista.adaptadores.AdaptadorHistorial;
import es.rbp.musica.vista.snackbar.SnackbarCancion;
import es.rbp.musica.vista.snackbar.SnackbarMusica;
import es.rbp.musica.vista.snackbar.SnackbarPlaylists;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCanciones;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorQuery;

public class BuscarActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, AdaptadorCanciones.OnCancionClick,
        SnackbarCancion.Accion, SnackbarPlaylists.Accion, AdaptadorHistorial.OnHistorialClicked {

    private static final String TAG = "BUSCAR ACTIVITY";

    private List<Cancion> todasCanciones;
    private List<Cancion> cancionesFiltradas;

    private List<String> historial;

    private SnackbarMusica snackbar;

    private EditText txtBuscar;

    private RecyclerView recyclerViewHistorial;

    private Cancion cancionSeleccionada;

    private AdaptadorHistorial adaptador;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window ventana = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            ventana.setDecorFitsSystemWindows(false);

        getWindow().setStatusBarColor(getColor(android.R.color.transparent));
        ventana.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ventana.setEnterTransition(new Fade());
        ventana.setExitTransition(new Fade());
        cargarAjustes();
        setContentView(R.layout.activity_buscar);
        cargarVista();
    }

    @Override
    public void onBackPressed() {
        if (snackbar != null) {
            snackbar.ocultar();
            snackbar = null;
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAtrasBuscar)
            finishAfterTransition();
        else if (v.getId() == R.id.btnEliminarHistorial) {
            accesoFichero.eliminarHistorial();
            actualizarRecyclerViewHistorial();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        query = s.toString().trim().toLowerCase();
        cancionesFiltradas = filtrarCancionesPorQuery(todasCanciones, query, ajustes);
        actualizarRecyclerViewCanciones();
    }

    @Override
    public void onMenuClicked(int indice) {
        cancionSeleccionada = cancionesFiltradas.get(indice);
        Log.i(TAG, "Canción seleccionada: " + cancionSeleccionada.getDatos());
        snackbar = new SnackbarCancion(this, findViewById(android.R.id.content), this, cancionSeleccionada, ajustes);

        if (query != null && !query.isEmpty() && !historial.contains(query))
            historial.add(0, query);

        accesoFichero.guardarHistorial(historial);
        adaptador.notifyItemInserted(historial.size() - 1);
        adaptador.notifyDataSetChanged();
        recyclerViewHistorial.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int indice) {

    }

    @Override
    public void realizarAccion(int accion) {
        switch (accion) {
            case SnackbarCancion.ACCION_ANADIR_A_FAVORITOS:
                accesoFichero.anadirFavorito(cancionSeleccionada.getDatos());
                snackbar = null;
                break;
            case SnackbarCancion.ACCION_ELIMINAR_DE_FAVORITOS:
                accesoFichero.eliminarFavorito(cancionSeleccionada.getDatos());
                snackbar = null;
                break;
            case SnackbarCancion.ACCION_OCULTAR:
                snackbar = null;
                break;
            case SnackbarCancion.ACCION_ANADIR_A_LA_PLAYLIST:
                Log.d("FragmentPLaylists", "añadir playlist");
                new Handler().postDelayed(this::mostrarPlaylists, 400);
                break;
            case SnackbarCancion.ACCION_ANADIR_A_LA_COLA:
                Cola cola = accesoFichero.getCola();
                if (cola.getListaCanciones().size() == 0)
                    cola.crearCola(cancionSeleccionada);
                else
                    cola.anadirALaCola(cancionSeleccionada);
                accesoFichero.guardarCola(cola);
                break;
            case SnackbarCancion.ACCION_REPRODUCIR_SIGUIENTE:
                Cola cola1 = accesoFichero.getCola();
                if (cola1.getListaCanciones().size() == 0)
                    cola1.crearCola(cancionSeleccionada);
                else
                    cola1.reproducirSiguiente(cancionSeleccionada);
                accesoFichero.guardarCola(cola1);
                snackbar = null;
                break;
        }
    }

    @Override
    public void onPlaylistSeleccionada(int indicePlaylist) {
        if (indicePlaylist != SnackbarPlaylists.NINGUNA_PLAYLIST) {
            Playlist playlist = accesoFichero.getPlaylistPorIndice(indicePlaylist);
            playlist.getCanciones().add(cancionSeleccionada.getDatos());
            accesoFichero.guardarPlaylist(playlist);
        }
        snackbar = null;
    }

    @Override
    public void onHistorialClick(int indice) {
        String texto = historial.get(indice);
        txtBuscar.setText(texto);
    }

    private void mostrarPlaylists() {
        List<Playlist> playlists = accesoFichero.getPlaylists();
        snackbar = new SnackbarPlaylists(this, findViewById(android.R.id.content), this, playlists);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cargarVista() {
        accesoFichero = AccesoFichero.getInstance(this);

        todasCanciones = filtrarCanciones(accesoFichero.getTodasCanciones(), ajustes);
        cancionesFiltradas = filtrarCanciones(todasCanciones, ajustes);

        txtBuscar = findViewById(R.id.txtBuscarCancion);
        txtBuscar.addTextChangedListener(this);

        ImageView btnAtras = findViewById(R.id.btnAtrasBuscar);
        btnAtras.setOnClickListener(this);

        LinearLayout btnEliminarHistorial = findViewById(R.id.btnEliminarHistorial);
        btnEliminarHistorial.setOnClickListener(this);

        actualizarRecyclerViewCanciones();

        actualizarRecyclerViewHistorial();
    }

    private void actualizarRecyclerViewCanciones() {
        Collections.sort(cancionesFiltradas);
        IndexFastScrollRecyclerView recyclerView = findViewById(R.id.recyclerViewBuscarCanciones);
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
        AdaptadorCanciones adaptador = new AdaptadorCanciones(cancionesFiltradas, this);
        recyclerView.setAdapter(adaptador);
    }

    private void actualizarRecyclerViewHistorial() {
        historial = accesoFichero.getHistorial();

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adaptador = new AdaptadorHistorial(historial, this);
        recyclerViewHistorial.setAdapter(adaptador);

        if (historial.size() == 0)
            recyclerViewHistorial.setVisibility(View.INVISIBLE);
    }

    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController wic = getWindow().getDecorView().getWindowInsetsController();
                wic.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            } else
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            setTheme(R.style.TemaClaro);
        }
    }
}