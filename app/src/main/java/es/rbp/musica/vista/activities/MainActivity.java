package es.rbp.musica.vista.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.vista.fragments.FragmentCarpetas;

import static es.rbp.musica.modelo.AccesoFichero.REQUEST_PERMISO_LECTURA;

/**
 * @author Ricardo Bordería Pi
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Main Activity";

    private FragmentCarpetas carpetas;

    private TextView lblTituloFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISO_LECTURA);
        else
            cargarAjustes();
        setContentView(R.layout.activity_main);
        cargarVista();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISO_LECTURA && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            cargarAjustes();
        else
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAjustes:
                abrirAjustes();
                break;
        }
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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.TemaClaro);
        }
    }

    private void cargarVista() {
        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(this);

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
    }
}