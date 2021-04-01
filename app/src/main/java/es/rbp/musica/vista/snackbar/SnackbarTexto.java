package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import es.rbp.musica.R;

public class SnackbarTexto extends Dialog implements View.OnClickListener {

    private EditText txtNombrePlaylist;

    private Accion accion;

    private int idTexto;

    public SnackbarTexto(Accion accion, Activity activity, int idTexto) {
        super(activity);
        this.accion = accion;
        this.idTexto = idTexto;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snackbar_texto);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setBackgroundDrawable(null);

        View opacityPane = findViewById(R.id.opacityPaneSnackbarCrearPlaylist);
        opacityPane.setOnClickListener(this);

        View contenedor = findViewById(R.id.contenedorSnackbarCrearPlaylist);
        contenedor.setOnClickListener(this);

        TextView lblTitulo = findViewById(R.id.lblTituloSnackbarTexto);
        lblTitulo.setText(idTexto);

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
