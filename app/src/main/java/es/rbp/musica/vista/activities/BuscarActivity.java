package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import es.rbp.musica.vista.snackbar.SnackbarCancion;
import es.rbp.musica.vista.snackbar.SnackbarMusica;
import es.rbp.musica.vista.snackbar.SnackbarPlaylists;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCanciones;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorQuery;

public class BuscarActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, AdaptadorCanciones.OnCancionClick,
        SnackbarCancion.Accion, SnackbarPlaylists.Accion {

    private List<Cancion> todasCanciones;
    private List<Cancion> cancionesFiltradas;

    private SnackbarMusica snackbar;

    private Cancion cancionSeleccionada;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window ventana = getWindow();
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
        cancionesFiltradas = filtrarCancionesPorQuery(todasCanciones, query, ajustes);
        actualizarRecyclerView();
    }

    @Override
    public void onMenuClicked(int indice) {
        cancionSeleccionada = cancionesFiltradas.get(indice);
        snackbar = new SnackbarCancion(this, findViewById(android.R.id.content), this, cancionSeleccionada, ajustes);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostrarPlaylists();
                    }
                }, 400);
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

    private void mostrarPlaylists() {
        List<Playlist> playlists = accesoFichero.getPlaylists();
        snackbar = new SnackbarPlaylists(this, findViewById(android.R.id.content), this, playlists);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cargarVista() {
        accesoFichero = AccesoFichero.getInstance(this);

        todasCanciones = filtrarCanciones(accesoFichero.getTodasCanciones(), ajustes);
        cancionesFiltradas = filtrarCanciones(todasCanciones, ajustes);

        EditText txtBuscar = findViewById(R.id.txtBuscarCancion);
        txtBuscar.addTextChangedListener(this);

        ImageView btnAtras = findViewById(R.id.btnAtrasBuscar);
        btnAtras.setOnClickListener(this);

        actualizarRecyclerView();
    }

    private void actualizarRecyclerView() {
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