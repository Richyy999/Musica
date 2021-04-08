package es.rbp.musica.vista.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
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
import es.rbp.musica.vista.snackbar.SnackbarTexto;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;
import static es.rbp.musica.modelo.entidad.Playlist.EXTRA_PLAYLIST;
import static es.rbp.musica.modelo.entidad.Playlist.INDICE_POR_DEFECTO;
import static es.rbp.musica.vista.activities.AnadirCancionesActivity.EXTRA_CANCIONES_ANADIDAS;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SnackbarTexto.Accion, SnackbarCancion.Accion, AdaptadorCanciones.OnCancionClick {

    public static final int CODIGO_REQUEST_PLAYLIST = 10;
    public static final int CODIGO_REQUEST_CAMBIAR_IMAGEN = 11;

    private static final String TAG = "ACTIVITY PLAYLIST";

    private SnackbarMusica snackbarMusica;

    private List<Cancion> canciones;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    private Playlist playlist;

    private Cancion cancionSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_playlist);
        cargarVista();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEliminarPlaylist:
                accesoFichero.eliminarPlaylist(playlist);
                finish();
                break;
            case R.id.btnAnadirCancionPlaylist:
                Intent intent = new Intent(this, AnadirCancionesActivity.class);
                startActivityForResult(intent, CODIGO_REQUEST_PLAYLIST);
                break;
            case R.id.btnAtrasPlaylist:
                finishAfterTransition();
                break;
            case R.id.imgPlaylist:
                mostrarToast(R.string.mantenParaCambiarImagen);
                break;
            case R.id.lblNombrePlaylist:
                mostrarToast(R.string.mantenerParaCambiarNombre);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.lblNombrePlaylist)
            cambiarNombrePlaylist();
        else if (v.getId() == R.id.imgPlaylist)
            elegirImagen();
        Vibrator vibrador = getSystemService(Vibrator.class);
        vibrador.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        return false;
    }

    @Override
    public void realizarAccion(String texto) {
        playlist.setNombre(texto);

        accesoFichero.guardarPlaylist(playlist);

        actualizarNombrePlaylist();
    }

    @Override
    public void realizarAccion(int accion) {
        switch (accion) {
            case SnackbarCancion.ACCION_ELIMINAR_DE_LA_PLAYLIST:
                eliminarCancion();
                break;
            case SnackbarCancion.ACCION_ANADIR_A_FAVORITOS:
                accesoFichero.anadirFavorito(cancionSeleccionada.getDatos());
                break;
            case SnackbarCancion.ACCION_ELIMINAR_DE_FAVORITOS:
                accesoFichero.eliminarFavorito(cancionSeleccionada.getDatos());
                break;
        }
        snackbarMusica = null;
    }

    @Override
    public void onMenuClicked(int indice) {
        cancionSeleccionada = canciones.get(indice);
        snackbarMusica = new SnackbarCancion(this, findViewById(android.R.id.content), this, cancionSeleccionada, ajustes);
    }

    @Override
    public void onClick(int indice) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_REQUEST_PLAYLIST) {
            if (resultCode == RESULT_OK) {
                String[] nombreCanciones = data.getStringArrayExtra(EXTRA_CANCIONES_ANADIDAS);
                List<String> listaNombreCanciones = new ArrayList<>();
                for (int i = 0; i < nombreCanciones.length; i++) {
                    Log.i(TAG, "Añadido: " + nombreCanciones[i]);
                    listaNombreCanciones.add(nombreCanciones[i]);
                }
                canciones.addAll(filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), listaNombreCanciones));
                playlist.getCanciones().addAll(listaNombreCanciones);
                accesoFichero.guardarPlaylist(playlist);
                actualizarRecyclerView();
            } else {
                Log.i(TAG, "No se han añadido canciones");
            }
        } else if (requestCode == CODIGO_REQUEST_CAMBIAR_IMAGEN) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uriImagen = data.getData();
                accesoFichero.guardarImagenPlaylist(playlist, uriImagen);

                actualizarImagenPlaylist();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (snackbarMusica != null) {
            snackbarMusica.ocultar();
            snackbarMusica = null;
        } else
            finishAfterTransition();
    }

    private void eliminarCancion() {
        int indiceCancion = canciones.indexOf(cancionSeleccionada);
        canciones.remove(cancionSeleccionada);
        playlist.eliminarCancion(indiceCancion);

        actualizarRecyclerView();

        accesoFichero.guardarPlaylist(playlist);
    }

    private void elegirImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Elige una imagen"), CODIGO_REQUEST_CAMBIAR_IMAGEN);
    }

    private void cambiarNombrePlaylist() {
        SnackbarTexto snackbarTexto = new SnackbarTexto(this, this, R.string.nombreDeLaPlaylist, R.string.introduceNombrePlaylist);
        snackbarMusica = snackbarTexto;
        snackbarTexto.show();
    }

    private void mostrarToast(int idTexto) {
        Toast.makeText(this, idTexto, Toast.LENGTH_SHORT).show();
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

    private void cargarVista() {
        accesoFichero = AccesoFichero.getInstance(this);
        int indicePlaylist = getIntent().getIntExtra(EXTRA_PLAYLIST, INDICE_POR_DEFECTO);
        playlist = accesoFichero.buscarPlaylistPorIndice(indicePlaylist);

        ImageView btnAtras = findViewById(R.id.btnAtrasPlaylist);
        btnAtras.setOnClickListener(this);

        LinearLayout btnEliminarPlaylist = findViewById(R.id.btnEliminarPlaylist);
        btnEliminarPlaylist.setOnClickListener(this);

        LinearLayout btnAnadirCanciones = findViewById(R.id.btnAnadirCancionPlaylist);
        btnAnadirCanciones.setOnClickListener(this);

        canciones = filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), playlist.getCanciones());

        actualizarRecyclerView();

        actualizarNombrePlaylist();

        actualizarImagenPlaylist();
    }

    private void actualizarRecyclerView() {
        String texto;
        if (playlist.getCanciones().size() == 1)
            texto = getString(R.string.unaCancion);
        else
            texto = playlist.getCanciones().size() + " " + getString(R.string.canciones);
        TextView lblNumCanciones = findViewById(R.id.lblNumCancionesPlaylist);
        lblNumCanciones.setText(texto);

        Collections.sort(canciones);
        IndexFastScrollRecyclerView recyclerView = findViewById(R.id.recyclerViewCancionesPlaylist);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorCanciones adaptador = new AdaptadorCanciones(canciones, this);
        recyclerView.setAdapter(adaptador);
    }

    private void actualizarNombrePlaylist() {
        TextView lblNombrePlaylist = findViewById(R.id.lblNombrePlaylist);
        lblNombrePlaylist.setText(playlist.getNombre());
        lblNombrePlaylist.setOnClickListener(this);
        lblNombrePlaylist.setOnLongClickListener(this);
        lblNombrePlaylist.setSelected(true);
    }

    private void actualizarImagenPlaylist() {
        ImageView imgPlaylist = findViewById(R.id.imgPlaylist);
        imgPlaylist.setOnClickListener(this);
        imgPlaylist.setOnLongClickListener(this);

        File imagenPlaylist = accesoFichero.getImagenPlaylist(playlist);
        if (imagenPlaylist == null)
            Glide.with(this).load(R.drawable.imagen_playlist).into(imgPlaylist);
        else
            Glide.with(this).load(BitmapFactory.decodeFile(imagenPlaylist.getPath())).into(imgPlaylist);
    }
}