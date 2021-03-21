package es.rbp.musica.vista.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import es.rbp.musica.R;

public class SnackbarAlerta implements View.OnClickListener {

    private Snackbar snackbar;

    private View opacityPane;

    private TextView btnCancelar;
    private TextView btnAceptar;

    private Accion accion;

    public SnackbarAlerta(Activity activity, View view, Accion accion, int idTituo, int idMensaje) {
        this.accion = accion;

        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        View vistaPersonalizada = activity.getLayoutInflater().inflate(R.layout.snackbar_alerta, null);

        View snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        // Para evitar que se oculte el snacbar al pulsar sobre el contenedor
        View contenedor = vistaPersonalizada.findViewById(R.id.snackbarLayout);
        contenedor.setOnClickListener(this);

        this.opacityPane = vistaPersonalizada.findViewById(R.id.opacityPaneSnackbarAlerta);
        this.opacityPane.setOnClickListener(this);
        this.opacityPane.setVisibility(View.INVISIBLE);

        this.btnAceptar = vistaPersonalizada.findViewById(R.id.btnOkSnackbarAlerta);
        this.btnAceptar.setOnClickListener(this);

        this.btnCancelar = vistaPersonalizada.findViewById(R.id.btnCancelarSnackbarAlerta);
        this.btnCancelar.setOnClickListener(this);

        TextView lblTitulo = vistaPersonalizada.findViewById(R.id.lblTituloSnackbarAlerta);
        lblTitulo.setText(idTituo);

        TextView lblMensaje = vistaPersonalizada.findViewById(R.id.lblMensajeSnackbarAlerta);
        lblMensaje.setText(idMensaje);

        layout.addView(vistaPersonalizada);
        layout.setPadding(0, 0, 0, 0);

        this.snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarOpacityPane();
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAceptar.getId())
            accion.realizarAccion();
        else if (v.getId() == btnCancelar.getId() || v.getId() == opacityPane.getId())
            ocultar();
    }

    private void mostrarOpacityPane() {
        AlphaAnimation animacion = new AlphaAnimation(0, 255);
        animacion.setDuration(200);
        this.opacityPane.setAlpha(0);
        this.opacityPane.setVisibility(View.VISIBLE);
        this.opacityPane.startAnimation(animacion);
        this.opacityPane.setAlpha(1);
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
