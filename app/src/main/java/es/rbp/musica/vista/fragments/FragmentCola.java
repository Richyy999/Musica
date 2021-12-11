package es.rbp.musica.vista.fragments;

import static android.app.Activity.RESULT_OK;
import static es.rbp.musica.modelo.AudioUtils.filtrarCancionesPorNombres;
import static es.rbp.musica.modelo.AudioUtils.getDatosCanciones;
import static es.rbp.musica.vista.activities.SeleccionaCancionesActivity.EXTRA_CANCIONES_ANADIDAS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.modelo.entidad.Playlist;
import es.rbp.musica.vista.activities.AnadirSnackbarMusica;
import es.rbp.musica.vista.activities.ReproductorActivity;
import es.rbp.musica.vista.activities.SeleccionaCancionesActivity;
import es.rbp.musica.vista.snackbar.SnackbarCola;
import es.rbp.musica.vista.snackbar.SnackbarTexto;

public class FragmentCola extends Fragment implements View.OnClickListener, SnackbarCola.Accion, SnackbarTexto.Accion {

    /**
     * Tag para el Log
     */
    private static final String TAG = "FRAGMENT_COLA";

    private View root;

    private TextView lblNombre;
    private TextView lblAlbum;

    private CircleImageView imgCancion;

    private ImageView btnCancionAnterior;
    private ImageView btnSiguienteCancion;

    private ImageView btnPlayPause;

    private ImageView btnCola;

    private AppCompatSeekBar seekBar;

    private ActivityResultLauncher<Intent> anadirCanciones;

    private Cola cola;

    private Cancion cancionActual;

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    private Context context;

    public FragmentCola() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        accesoFichero = AccesoFichero.getInstance(context);
        cola = accesoFichero.getCola();
        ajustes = Ajustes.getInstance(context);
        cancionActual = cola.getCancionActual();

        root = inflater.inflate(R.layout.fragment_cola, container, false);
        cargarVista();

        anadirCanciones = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String accion = data.getAction();
                        String[] nombreCanciones = data.getStringArrayExtra(EXTRA_CANCIONES_ANADIDAS);
                        assert accion != null;
                        if (accion.equals(SeleccionaCancionesActivity.ACCION_ANADIR)) {
                            List<String> listaNombreCanciones = new ArrayList<>();
                            assert nombreCanciones != null;
                            for (String nombreCancione : nombreCanciones) {
                                Log.i(TAG, "Añadido: " + nombreCancione);
                                listaNombreCanciones.add(nombreCancione);
                            }
                            cola.anadirALaCola(filtrarCancionesPorNombres(accesoFichero.getTodasCanciones(), listaNombreCanciones));
                            accesoFichero.guardarCola(cola);
                        } else {
                            Log.i(TAG, "No se han añadido canciones");
                        }
                        ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(new SnackbarCola(getActivity(),
                                getActivity().findViewById(android.R.id.content), ajustes, cola, this));
                    }
                });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        cancionActual = cola.getCancionActual();
        actualizarVista();
        actualizarSeekbar(cola.getProgresoActual());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == root.getId()) {
            if (cola.getListaCanciones().size() > 0) {
                Intent intent = new Intent(context, ReproductorActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.btnMostrarColaFragment) {
            if (cola.getListaCanciones().size() > 0) {
                SnackbarCola snackbarCola = new SnackbarCola(getActivity(), getActivity().findViewById(android.R.id.content), ajustes, cola, this);
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(snackbarCola);
            }
        } else if (id == R.id.btnSiguienteCancionFragment) {
            cancionActual = cola.siguienteCancion();
            if (cancionActual != null) {
                actualizarVista();
            }
        } else if (id == R.id.btnCancionAnteriorFragment) {
            cancionActual = cola.getCancionAnterior();
            if (cancionActual != null) {
                actualizarVista();
            }
        }
    }

    @Override
    public void realizarAccionCola(int accion) {
        if (accion == SnackbarCola.ACCION_OCULTAR)
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        else if (accion == SnackbarCola.ACCION_ELIMINAR_COLA) {
            cola.eliminarCola();
            accesoFichero.guardarCola(cola);
            reiniciarVista();
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        } else if (accion == SnackbarCola.ACCION_GUARDAR_COLA) {
            ((AnadirSnackbarMusica) getActivity()).cerrar();
            new Handler().postDelayed(() -> {
                SnackbarTexto snackbarTexto = new SnackbarTexto(FragmentCola.this,
                        getActivity(), R.string.nombreDeLaPlaylist,
                        R.string.introduceNombrePlaylist);
                snackbarTexto.show();
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(snackbarTexto);
            }, 400);
        } else if (accion == SnackbarCola.ACCION_ANADIR_CANCIONES) {
            ((AnadirSnackbarMusica) getActivity()).cerrar();
            Intent intent = new Intent(getContext(), SeleccionaCancionesActivity.class);
            intent.putExtra(SeleccionaCancionesActivity.EXTRA_MODO_SELECCION, SeleccionaCancionesActivity.ACCION_ANADIR);
            anadirCanciones.launch(intent);
        }
    }

    @Override
    public void realizarAccion(String texto) {
        if (texto != null) {
            try {
                Playlist playlist = accesoFichero.crearPlaylist(texto, ajustes);
                playlist.setCanciones(getDatosCanciones(cola.getListaCanciones()));
                accesoFichero.guardarPlaylist(playlist);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
        ((AnadirSnackbarMusica) getActivity()).cerrar();
    }

    private void reiniciarVista() {
        seekBar.setProgress(0);

        lblNombre.setText(R.string.app_name);

        lblAlbum.setText(R.string.album_desconocido);

        imgCancion.setImageResource(R.drawable.imagen_playlist);

        if (ajustes.isModoOscuro())
            btnPlayPause.setImageResource(R.drawable.icono_play_fragment_oscuro);
        else
            btnPlayPause.setImageResource(R.drawable.icono_play_fragment_claro);
    }

    private void actualizarSeekbar(int progreso) {
        if (cancionActual != null)
            seekBar.setMax(cancionActual.getDuracion());
        seekBar.setProgress(progreso);
    }

    private void actualizarVista() {
        if (cancionActual != null) {
            lblAlbum.setText(cancionActual.getAlbum());
            Glide.with(context).load(cancionActual.getImagenAlbum(getContext())).into(imgCancion);
        }

        if (cola.getListaCanciones().size() > 0) {
            if (ajustes.isUtilizarNombreDeArchivo())
                lblNombre.setText(cancionActual.getNombreArchivo());
            else
                lblNombre.setText(cancionActual.getNombre());

            seekBar.setProgress(cola.getProgresoActual());
        } else
            reiniciarVista();
    }

    private void cargarVista() {
        root.setOnClickListener(this);

        seekBar = root.findViewById(R.id.seekbarFragment);
        seekBar.setEnabled(false);
        seekBar.getThumb().setAlpha(0);

        lblNombre = root.findViewById(R.id.lblNombreCancionFragment);
        lblNombre.setSelected(true);

        lblAlbum = root.findViewById(R.id.lblAlbumFragmentCola);

        imgCancion = root.findViewById(R.id.imgAlbumFragment);

        btnCancionAnterior = root.findViewById(R.id.btnCancionAnteriorFragment);
        btnCancionAnterior.setOnClickListener(this);

        btnSiguienteCancion = root.findViewById(R.id.btnSiguienteCancionFragment);
        btnSiguienteCancion.setOnClickListener(this);

        btnPlayPause = root.findViewById(R.id.btnPlayPausaFragment);
        btnPlayPause.setOnClickListener(this);

        btnCola = root.findViewById(R.id.btnMostrarColaFragment);
        btnCola.setOnClickListener(this);
    }
}
