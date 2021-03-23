package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.adaptadores.AdaptadorCanciones;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;
import static es.rbp.musica.modelo.entidad.Playlist.EXTRA_PLAYLIST;
import static es.rbp.musica.modelo.entidad.Playlist.INDICE_POR_DEFECTO;

public class PlaylistActivity extends AppCompatActivity {

    private List<Cancion> canciones;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    private Playlist playlist;

    private AdaptadorCanciones adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_playlist);
        cargarVista();
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

        TextView lblNombrePlaylist = findViewById(R.id.lblNombrePlaylist);
        lblNombrePlaylist.setText(playlist.getNombre());
        lblNombrePlaylist.setSelected(true);

        String texto;
        if (playlist.getCanciones().size() == 1)
            texto = getString(R.string.unaCancion);
        else
            texto = playlist.getCanciones().size() + " " + getString(R.string.canciones);
        TextView lblNumCanciones = findViewById(R.id.lblNumCancionesPlaylist);
        lblNumCanciones.setText(texto);

        ImageView imgPlaylist = findViewById(R.id.imgPlaylist);
        String rutaImagen = playlist.getRutaImagen();
        if (rutaImagen == null)
            Glide.with(this).load(R.drawable.imagen_playlist).into(imgPlaylist);
        else
            Glide.with(this).load(rutaImagen).into(imgPlaylist);

        canciones = filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), playlist.getCanciones());
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
        adaptador = new AdaptadorCanciones(canciones);
        recyclerView.setAdapter(adaptador);
    }
}