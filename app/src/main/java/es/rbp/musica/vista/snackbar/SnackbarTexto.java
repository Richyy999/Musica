package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.rbp.musica.R;

public class SnackbarTexto extends Dialog implements SnackbarMusica, View.OnClickListener {

    private EditText txtSnackbarTexto;

    private Accion accion;

    private int idTexto;
    private int idAviso;

    public SnackbarTexto(Accion accion, Activity activity, int idTexto, int idAviso) {
        super(activity);
        this.accion = accion;
        this.idTexto = idTexto;
        this.idAviso = idAviso;
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

        this.txtSnackbarTexto = findViewById(R.id.txtNombrePlaylist);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOkSnackbarCrearPlaylist) {
            String texto = txtSnackbarTexto.getText().toString().trim();
            if (texto.equals(""))
                Toast.makeText(getContext(), idAviso, Toast.LENGTH_SHORT).show();
            else {
                accion.realizarAccion(texto);
                ocultar();
            }
        } else if (v.getId() != R.id.contenedorSnackbarCrearPlaylist)
            ocultar();
    }

    @Override
    public void ocultar() {
        dismiss();
    }

    public interface Accion {
        void realizarAccion(String texto);
    }
}
