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

public class FragmentCola extends Fragment implements View.OnClickListener {

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
    public void onClick(View v) {
        int id = v.getId();
        if (id == root.getId()) {
            if (cola != Cola.COLA_VACIA) {
                Intent intent = new Intent(context, ReproductorActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.btnMostrarColaFragment) {
            if (!cola.equals(Cola.COLA_VACIA)) {
                SnackbarCola snackbarCola = new SnackbarCola(getActivity(), getActivity().findViewById(android.R.id.content), cola.getListaCanciones());
                ((AnadirSnackbarMusica) getActivity()).anadirSnackbarMusica(snackbarCola);
            }
        }
    }

    private void cargarVista() {
        root.setOnClickListener(this);

        seekBar = root.findViewById(R.id.seekbarFragment);
        seekBar.setEnabled(false);
        seekBar.getThumb().setAlpha(0);

        lblNombre = root.findViewById(R.id.lblNombreCancionFragment);
        lblNombre.setSelected(true);
        if (cola != Cola.COLA_VACIA) {
            if (ajustes.isUtilizarNombreDeArchivo())
                lblNombre.setText(cancionActual.getNombreArchivo());
            else
                lblNombre.setText(cancionActual.getNombre());
        }

        lblAlbum = root.findViewById(R.id.lblAlbumFragmentCola);

        imgCancion = root.findViewById(R.id.imgAlbumFragment);

        if (cancionActual != null) {
            lblAlbum.setText(cancionActual.getAlbum());
            Glide.with(context).load(cancionActual.getImagenAlbum(getContext())).into(imgCancion);
        }

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
