package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;

public class ReproductorActivity extends AppCompatActivity implements View.OnClickListener {

    private Ajustes ajustes;

    private Cancion cancion;

    private Cola cola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        cargarAjustes();
        setContentView(R.layout.activity_reproductor);
        cola = AccesoFichero.getInstance(this).getCola();
        cargarVista();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAtrasReproductor:
                finish();
                break;
            case R.id.btnCancionAnteriorActivity:
                cancion = cola.cancionAnterior();
                if (cancion != null)
                    cargarVista();
                break;
            case R.id.btnSiguienteCancionActivity:
                cancion = cola.siguienteCancion();
                if (cancion != null)
                    cargarVista();
                break;
        }
    }

    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.AnimacionAbajoArribaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.AnimacionAbajoArribaClaro);
        }
    }

    private void cargarVista() {
        cancion = cola.getCancionActual();

        ImageView btnAtras = findViewById(R.id.btnAtrasReproductor);
        btnAtras.setOnClickListener(this);

        RoundedImageView imgCancion = findViewById(R.id.imgCancion);
        Bitmap imagenCancion = cancion.getImagenAlbum(this);
        Glide.with(this).load(imagenCancion).into(imgCancion);

        TextView lblNombreCancion = findViewById(R.id.lblNombreCancionReproductor);
        lblNombreCancion.setSelected(true);
        if (ajustes.isUtilizarNombreDeArchivo())
            lblNombreCancion.setText(cancion.getNombreArchivo());
        else
            lblNombreCancion.setText(cancion.getNombre());

        ImageView btnCancionAnterior = findViewById(R.id.btnCancionAnteriorActivity);
        btnCancionAnterior.setOnClickListener(this);

        ImageView btnSiguienteCancion = findViewById(R.id.btnSiguienteCancionActivity);
        btnSiguienteCancion.setOnClickListener(this);
    }
}