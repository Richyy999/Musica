package es.rbp.musica.vista.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.vista.snackbar.SnackbarAlerta;

import static es.rbp.musica.modelo.Ajustes.MAX_FILTRO_DURACION;
import static es.rbp.musica.modelo.Ajustes.MAX_FILTRO_TAMANO;
import static es.rbp.musica.modelo.Ajustes.SIN_FILTRO;

public class AjustesActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, SnackbarAlerta.Accion {

    private Ajustes ajustes;

    private AppCompatSeekBar seekbarTamano;
    private AppCompatSeekBar seekbarDuracion;

    private SwitchCompat switchModoOscuro;
    private SwitchCompat switchFiltrarTamano;
    private SwitchCompat switchFiltrarDuracion;
    private SwitchCompat switchUtilizarNombreDeArchivo;

    private TextView lblfiltroTamano;
    private TextView lblfiltroDuracion;

    private ConstraintLayout seccionTamano;
    private ConstraintLayout seccionDuracion;

    private ConstraintLayout seccionGrandeTamano;
    private ConstraintLayout seccionGrandeDuracion;
    private ConstraintLayout seccionGrandeModoOscuro;
    private ConstraintLayout seccionGrandeCarpetas;
    private ConstraintLayout seccionGrandeRestablecer;
    private ConstraintLayout seccionGrandeUtilizarNombreDeArchivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cargarAjustes();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        cargarVista();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ajustes.guardarAjustes(AjustesActivity.this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == switchModoOscuro.getId()) {
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            ajustes.setModoOscuro(isChecked);
            ajustes.guardarAjustes(AjustesActivity.this);
        } else if (buttonView.getId() == switchFiltrarTamano.getId()) {
            if (!isChecked) {
                seccionTamano.setVisibility(View.GONE);
                ajustes.actualizarFiltroTamano(SIN_FILTRO);
            } else {
                seccionTamano.setVisibility(View.VISIBLE);
                ajustes.actualizarFiltroTamano(ajustes.getUltimoFiltroTamano());
                seekbarTamano.setProgress(ajustes.getFiltroTanamoActual());
                actualizarTextView();
            }
        } else if (buttonView.getId() == switchFiltrarDuracion.getId()) {
            if (!isChecked) {
                seccionDuracion.setVisibility(View.GONE);
                ajustes.actualizarFiltroDuracion(SIN_FILTRO);
            } else {
                seccionDuracion.setVisibility(View.VISIBLE);
                ajustes.actualizarFiltroDuracion(ajustes.getUltimoFiltroDuracion());
                seekbarDuracion.setProgress(ajustes.getFiltroDuracionActual());
                actualizarTextView();
            }
        } else if (buttonView.getId() == switchUtilizarNombreDeArchivo.getId())
            ajustes.setUtilizarNombreDeArchivo(isChecked);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnVolverAjustes)
            volver();
        else if (v.getId() == seccionGrandeTamano.getId())
            switchFiltrarTamano.setChecked(!switchFiltrarTamano.isChecked());
        else if (v.getId() == seccionGrandeDuracion.getId())
            switchFiltrarDuracion.setChecked(!switchFiltrarDuracion.isChecked());
        else if (v.getId() == seccionGrandeCarpetas.getId())
            abrirCarpetas();
        else if (v.getId() == seccionGrandeModoOscuro.getId())
            switchModoOscuro.setChecked(!switchModoOscuro.isChecked());
        else if (v.getId() == seccionGrandeRestablecer.getId())
            mostrarDialog();
        else if (v.getId() == seccionGrandeUtilizarNombreDeArchivo.getId())
            switchUtilizarNombreDeArchivo.setChecked(!switchUtilizarNombreDeArchivo.isChecked());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == seekbarTamano.getId() && fromUser) {
            ajustes.actualizarFiltroTamano(progress);
        } else if (seekBar.getId() == seekbarDuracion.getId() && fromUser) {
            ajustes.actualizarFiltroDuracion(progress);
        }
        actualizarTextView();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        volver();
    }

    @Override
    public void realizarAccion() {
        ajustes.restablecerAjustes(this);

        Intent intent = new Intent(this, AjustesActivity.class);
        startActivity(intent);
        finish();
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
        switchModoOscuro = findViewById(R.id.switchModoOscuro);
        switchModoOscuro.setChecked(ajustes.isModoOscuro());
        switchModoOscuro.setOnCheckedChangeListener(this);

        switchFiltrarTamano = findViewById(R.id.switchFiltrarTamano);
        switchFiltrarTamano.setChecked(ajustes.getFiltroTanamoActual() != SIN_FILTRO);
        switchFiltrarTamano.setOnCheckedChangeListener(this);

        switchFiltrarDuracion = findViewById(R.id.switchFiltrarDuracion);
        switchFiltrarDuracion.setChecked(ajustes.getFiltroDuracionActual() != SIN_FILTRO);
        switchFiltrarDuracion.setOnCheckedChangeListener(this);

        switchUtilizarNombreDeArchivo = findViewById(R.id.switchNombreArchivo);
        switchUtilizarNombreDeArchivo.setChecked(ajustes.isUtilizarNombreDeArchivo());
        switchUtilizarNombreDeArchivo.setOnCheckedChangeListener(this);

        seekbarTamano = findViewById(R.id.seekbarTamano);
        seekbarTamano.setMax(MAX_FILTRO_TAMANO);
        seekbarTamano.setProgress(ajustes.getFiltroTanamoActual());
        seekbarTamano.getThumb().setAlpha(0);
        seekbarTamano.setOnSeekBarChangeListener(this);

        seekbarDuracion = findViewById(R.id.seekbarDuracion);
        seekbarDuracion.setMax(MAX_FILTRO_DURACION);
        seekbarDuracion.setProgress(ajustes.getFiltroDuracionActual());
        seekbarDuracion.getThumb().setAlpha(0);
        seekbarDuracion.setOnSeekBarChangeListener(this);

        lblfiltroTamano = findViewById(R.id.lblDescFiltroTamano);

        lblfiltroDuracion = findViewById(R.id.lblDescFiltroDuracion);

        actualizarTextView();

        seccionTamano = findViewById(R.id.seccionTamano);
        if (ajustes.getFiltroTanamoActual() == SIN_FILTRO)
            seccionTamano.setVisibility(View.GONE);

        seccionDuracion = findViewById(R.id.seccionDuracion);
        if (ajustes.getFiltroDuracionActual() == SIN_FILTRO)
            seccionDuracion.setVisibility(View.GONE);

        seccionGrandeTamano = findViewById(R.id.seccionGrandeTamano);
        seccionGrandeTamano.setOnClickListener(this);

        seccionGrandeDuracion = findViewById(R.id.seccionGrandeDuracion);
        seccionGrandeDuracion.setOnClickListener(this);

        seccionGrandeModoOscuro = findViewById(R.id.seccionGrandeModoOscuro);
        seccionGrandeModoOscuro.setOnClickListener(this);

        seccionGrandeCarpetas = findViewById(R.id.btnFiltrarCarpetas);
        seccionGrandeCarpetas.setOnClickListener(this);

        seccionGrandeRestablecer = findViewById(R.id.seccionGrandeRestablecer);
        seccionGrandeRestablecer.setOnClickListener(this);

        seccionGrandeUtilizarNombreDeArchivo = findViewById(R.id.seccionGrandeNombreArchivos);
        seccionGrandeUtilizarNombreDeArchivo.setOnClickListener(this);

        ImageView btnVolver = findViewById(R.id.btnVolverAjustes);
        btnVolver.setOnClickListener(this);
    }

    private void actualizarTextView() {
        int kb = ajustes.getFiltroTanamoActual();
        double mb = (double) kb / 1024;
        String textoTamano;
        if (mb >= 0.8) {
            try {
                textoTamano = getString(R.string.ocultarArchivosMenoresA) + String.valueOf(mb).substring(0, 4) + "MB";
            } catch (IndexOutOfBoundsException e) {
                textoTamano = getString(R.string.ocultarArchivosMenoresA) + mb + "MB";
            }
        } else
            textoTamano = getString(R.string.ocultarArchivosMenoresA) + kb + "KB";
        lblfiltroTamano.setText(textoTamano);

        int duracion = ajustes.getFiltroDuracionActual();
        int min = duracion / 60;
        int seg = duracion % 60;
        String textoDuracion = getString(R.string.ocultarArchivosQueDurenMenosDe) + min + ":" + seg;
        lblfiltroDuracion.setText(textoDuracion);
    }

    private void abrirCarpetas() {
        Intent intent = new Intent(this, FiltroCarpetasActivity.class);
        startActivity(intent);
    }

    private void mostrarDialog() {
        new SnackbarAlerta(this, findViewById(android.R.id.content), this, R.string.restablecerAjustes, R.string.mensajeDialogRestablecerAjustes);
    }
}