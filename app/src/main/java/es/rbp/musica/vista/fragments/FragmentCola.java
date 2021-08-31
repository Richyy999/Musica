package es.rbp.musica.vista.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;
import es.rbp.musica.vista.activities.AnadirSnackbarMusica;
import es.rbp.musica.vista.activities.ReproductorActivity;
import es.rbp.musica.vista.snackbar.SnackbarCola;

public class FragmentCola extends Fragment implements View.OnClickListener, SnackbarCola.Accion {

    private View root;

    private TextView lblNombre;
    private TextView lblAlbum;

    private CircleImageView imgCancion;

    private ImageView btnCancionAnterior;
    private ImageView btnSiguienteCancion;

    private ImageView btnPlayPause;

    private ImageView btnCola;

    private AppCompatSeekBar seekBar;

    private Cola cola;

    private Cancion cancionActual;

    private Ajustes ajustes;

    private Context context;

    public FragmentCola() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        cola = AccesoFichero.getInstance(context).getCola();
        ajustes = Ajustes.getInstance(context);
        cancionActual = cola.getCancionActual();

        Log.d("FRAGMENT COLA", "Creado nuevo");

        root = inflater.inflate(R.layout.fragment_cola, container, false);
        cargarVista();
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
        }
    }

    @Override
    public void realizarAccionCola(int accion) {
        if (accion == SnackbarCola.ACCION_OCULTAR)
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        else if (accion == SnackbarCola.ACCION_ELIMINAR_COLA) {
            cola.eliminarCola();
            AccesoFichero.getInstance(getContext()).guardarCola(cola);
            reiniciarVista();
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        } else if (accion == SnackbarCola.ACCION_GUARDAR_COLA) {
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        } else if (accion == SnackbarCola.ACCION_ANADIR_CANCIONES) {
            ((AnadirSnackbarMusica) getActivity()).cerrar();
        }
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
