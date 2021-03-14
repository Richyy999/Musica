package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;

public class SnackbarAlerta implements View.OnClickListener {

    private Activity activity;

    private Snackbar snackbar;

    private Snackbar.SnackbarLayout layout;

    private View opacityPane;
    private View snackbarView;

    private TextView btnCancelar;
    private TextView btnAceptar;

    private TextView lblTitulo;
    private TextView lblMensaje;

    private Accion accion;

    private int idTituo;
    private int idMensaje;

    public SnackbarAlerta(Activity activity, View view, Accion accion, int idTituo, int idMensaje) {
        this.activity = activity;
        this.idTituo = idTituo;
        this.idMensaje = idMensaje;
        this.accion = accion;

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        this.snackbarView = this.snackbar.getView();
        this.snackbarView.setBackground(null);

        this.layout = (Snackbar.SnackbarLayout) snackbar.getView();
        this.layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_alerta, null);

        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.btnAceptar = vistaPersonalizada.findViewById(R.id.btnOkSnackbarAlerta);
        this.btnAceptar.setOnClickListener(this);
        this.btnCancelar = vistaPersonalizada.findViewById(R.id.btnCancelarSnackbarAlerta);
        this.btnCancelar.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarAlerta);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.GONE);

        this.lblTitulo = vistaPersonalizada.findViewById(R.id.lblTituloSnackbarAlerta);
        this.lblTitulo.setText(idTituo);
        this.lblMensaje = vistaPersonalizada.findViewById(R.id.lblMensajeSnackbarAlerta);
        this.lblMensaje.setText(idMensaje);

        this.layout.addView(vistaPersonalizada);
        this.layout.setPadding(0, 0, 0, 0);

        this.snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarOpacityPane();
            }
        }, 200);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAceptar.getId())
            accion.realizarAccion();
        if (v.getId() == btnAceptar.getId() || v.getId() == btnCancelar.getId() || v.getId() == opacityPane.getId())
            ocultar();
    }

    private void mostrarOpacityPane() {
        AlphaAnimation animacion = new AlphaAnimation(0, 255);
        animacion.setDuration(200);
        this.opacityPane.setVisibility(View.VISIBLE);
        this.opacityPane.startAnimation(animacion);
    }

    private void ocultar() {
        AlphaAnimation animacion = new AlphaAnimation(255, 0);
        animacion.setDuration(300);
        this.opacityPane.startAnimation(animacion);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                opacityPane.setVisibility(View.GONE);
                snackbar.dismiss();
            }
        }, 300);
    }

    public interface Accion {
        void realizarAccion();
    }
}
