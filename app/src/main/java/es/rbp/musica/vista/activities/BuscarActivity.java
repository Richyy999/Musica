package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;

public class BuscarActivity extends AppCompatActivity {

    private Ajustes ajustes;

    private AccesoFichero accesoFichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cargarAjustes();
        setContentView(R.layout.activity_buscar);
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
}