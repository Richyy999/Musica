package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import es.rbp.musica.R;

public class SnackbarCrearPlaylist extends Dialog implements View.OnClickListener {

    private EditText txtNombrePlaylist;

    private Accion accion;

    public SnackbarCrearPlaylist(Accion accion, Activity activity) {
        super(activity);
        this.accion = accion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snackbar_crear_playlist);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setBackgroundDrawable(null);

        View opacityPane = findViewById(R.id.opacityPaneSnackbarCrearPlaylist);
        opacityPane.setOnClickListener(this);

        View contenedor = findViewById(R.id.contenedorSnackbarCrearPlaylist);
        contenedor.setOnClickListener(this);

        TextView btnAceptar = findViewById(R.id.btnOkSnackbarCrearPlaylist);
        btnAceptar.setOnClickListener(this);

        TextView btnCancelar = findViewById(R.id.btnCancelarSnackbarCrearPlaylist);
        btnCancelar.setOnClickListener(this);

        this.txtNombrePlaylist = findViewById(R.id.txtNombrePlaylist);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOkSnackbarCrearPlaylist)
            accion.crearPlaylist(txtNombrePlaylist.getText().toString());

        if (v.getId() != R.id.contenedorSnackbarCrearPlaylist)
            dismiss();
    }

    public interface Accion {
        void crearPlaylist(String nombrePlaylist);
    }
}
