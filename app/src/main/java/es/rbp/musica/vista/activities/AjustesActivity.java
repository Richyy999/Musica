package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;

public class AjustesActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Ajustes ajustes;

    private ImageView btnVolver;

    private SwitchCompat modoOscuro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cargarAjustes();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        cargarVista();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == modoOscuro.getId()) {
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            ajustes.setModoOscuro(isChecked);
            ajustes.guardarAjustes(AjustesActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnVolverAjustes)
            volver();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        volver();
    }

    private void volver() {
        ajustes.guardarAjustes(AjustesActivity.this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void cargarAjustes() {
        ajustes = Ajustes.getInstance(this);
        if (ajustes.isModoOscuro()) {
            setTheme(R.style.TemaOscuro);
        } else {
            setTheme(R.style.TemaClaro);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void cargarVista() {
        modoOscuro = findViewById(R.id.switchModoOscuro);
        modoOscuro.setOnCheckedChangeListener(this);
        modoOscuro.setChecked(ajustes.isModoOscuro());

        btnVolver = findViewById(R.id.btnVolverAjustes);
        btnVolver.setOnClickListener(this);
    }
}