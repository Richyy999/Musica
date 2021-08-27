package es.rbp.musica.vista.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Carpeta;
import es.rbp.musica.vista.fragments.FragmentCarpetas;
import es.rbp.musica.vista.fragments.FragmentFavoritos;
import es.rbp.musica.vista.fragments.FragmentPlaylist;
import es.rbp.musica.vista.snackbar.SnackbarMusica;

import static es.rbp.musica.modelo.AccesoFichero.REQUEST_PERMISO_LECTURA;

/**
 * @author Ricardo Bordería Pi
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, AnadirSnackbarMusica {

    private SnackbarMusica snackbarMusica;

    private Fragment carpetas;

    private TextView lblTituloFragment;

    private View btnBuscar;

    private boolean carpetaAbierta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AccesoFichero.getInstance(this);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            getWindow().setDecorFitsSystemWindows(false);

        getWindow().setStatusBarColor(getColor(android.R.color.transparent));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISO_LECTURA);
        else {
            cargarAjustes();
            cargarVista();
        }
    }

    @Override
    public void onBackPressed() {
        if (snackbarMusica != null) {
            snackbarMusica.ocultar();
            snackbarMusica = null;
        } else if (carpetaAbierta)
            cargarFragmentCarpetas();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISO_LECTURA && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
            cargarAjustes();
            cargarVista();
        } else
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAjustes:
                abrirAjustes();
                break;
            case R.id.btnCarpeta:
                cargarFragmentCarpetas();
                break;
            case R.id.btnFavoritos:
                cargarFavoritos();
                break;
            case R.id.btnPlaylist:
                cargarPlaylists();
                break;
            case R.id.txtBuscar:
                abrirBuscar();
        }
    }

    @Override
    public void anadirSnackbarMusica(SnackbarMusica snackbarMusica) {
        this.snackbarMusica = snackbarMusica;
    }

    @Override
    public void cerrar() {
        this.snackbarMusica.ocultar();
        this.snackbarMusica = null;
    }

    private void abrirBuscar() {
        Intent intent = new Intent(this, BuscarActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, btnBuscar, ViewCompat.getTransitionName(btnBuscar));
        startActivity(intent, options.toBundle());
    }

    private void abrirAjustes() {
        Intent intent = new Intent(this, AjustesActivity.class);
        startActivity(intent);
        finish();
    }

    private void cargarAjustes() {
        Ajustes ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController wic = getWindow().getDecorView().getWindowInsetsController();
                wic.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            } else
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.TemaClaro);
        }
    }

    private void cargarVista() {
        setContentView(R.layout.activity_main);
        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(this);

        LinearLayout btnCarpetas = findViewById(R.id.btnCarpeta);
        btnCarpetas.setOnClickListener(this);

        LinearLayout btnFavoritos = findViewById(R.id.btnFavoritos);
        btnFavoritos.setOnClickListener(this);

        LinearLayout btnPlaylist = findViewById(R.id.btnPlaylist);
        btnPlaylist.setOnClickListener(this);

        btnBuscar = findViewById(R.id.txtBuscar);
        btnBuscar.setOnClickListener(this);

        lblTituloFragment = findViewById(R.id.lblTituloMenu);
        cargarFragmentCarpetas();
    }

    private void cargarFragmentCarpetas() {
        lblTituloFragment.setText(R.string.carpetas);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (carpetas == null)
            carpetas = new FragmentCarpetas();

        transaction.replace(R.id.contenedorFragment, carpetas);
        transaction.commit();

        carpetaAbierta = false;
    }

    public void cargarFavoritos(Carpeta carpeta) {
        lblTituloFragment.setText(carpeta.getNombre());
        Fragment fragment = new FragmentFavoritos(carpeta.getCanciones());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.contenedorFragment, fragment);
        transaction.commit();

        carpetaAbierta = true;
    }

    public void cargarFavoritos() {
        lblTituloFragment.setText(R.string.favoritos);

        Fragment fragment = new FragmentFavoritos();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.contenedorFragment, fragment);
        transaction.commit();

        carpetaAbierta = false;
    }

    private void cargarPlaylists() {
        lblTituloFragment.setText(R.string.playlist);

        Fragment fragment = new FragmentPlaylist();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.contenedorFragment, fragment);
        transaction.commit();

        carpetaAbierta = false;
    }
}