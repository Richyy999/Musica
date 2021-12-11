package es.rbp.musica.vista.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Carpeta;

public class AdaptadorFiltrarCarpeta extends RecyclerView.Adapter<AdaptadorFiltrarCarpeta.MyHolder> {

    private static final String TAG = "Adaptador filtrar carpetas";

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnCarpetaClick onCarpetaClick;

        private TextView lblNombreCarpeta, lblNumCaniones, lblRuta;

        private ImageView imgMostrar;

        public MyHolder(@NonNull View itemView, OnCarpetaClick onCarpetaClick) {
            super(itemView);
            itemView.setOnClickListener(this);

            this.onCarpetaClick = onCarpetaClick;

            lblNombreCarpeta = itemView.findViewById(R.id.lblNombreCarpeta);
            lblNumCaniones = itemView.findViewById(R.id.lblNumCanciones);
            lblRuta = itemView.findViewById(R.id.lblRutaCarpeta);

            imgMostrar = itemView.findViewById(R.id.imgOcultar);
        }

        @Override
        public void onClick(View v) {
            onCarpetaClick.onClick(getBindingAdapterPosition());
        }
    }

    private List<Carpeta> carpetas;

    private List<String> carpetasOcultas;

    private OnCarpetaClick onCarpetaClick;

    private Context context;

    public AdaptadorFiltrarCarpeta(List<Carpeta> carpetas, List<String> carpetasOcultas, OnCarpetaClick onCarpetaClick, Context context) {
        this.carpetas = carpetas;
        this.onCarpetaClick = onCarpetaClick;
        this.carpetasOcultas = carpetasOcultas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_carpetas, parent, false);
        return new MyHolder(v, onCarpetaClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Carpeta carpeta = carpetas.get(position);

        holder.lblNombreCarpeta.setText(carpeta.getNombre());
        holder.lblRuta.setText(carpeta.getRuta());
        if (carpeta.getCanciones().size() == 1)
            holder.lblNumCaniones.setText(R.string.unaCancion);
        else {
            String texto = carpeta.getCanciones().size() + " canciones";
            holder.lblNumCaniones.setText(texto);
        }

        Ajustes ajustes = Ajustes.getInstance(context);
        if (carpetasOcultas.contains(carpeta.getRuta()) && ajustes.isModoOscuro())
            holder.imgMostrar.setImageResource(R.drawable.icono_ocultar_oscuro);
        else if (carpetasOcultas.contains(carpeta.getRuta()) && !ajustes.isModoOscuro())
            holder.imgMostrar.setImageResource(R.drawable.icono_ocultar_claro);
        else if (!carpetasOcultas.contains(carpeta.getRuta()) && ajustes.isModoOscuro())
            holder.imgMostrar.setImageResource(R.drawable.icono_mostrar_oscuro);
        else if (!carpetasOcultas.contains(carpeta.getRuta()) && !ajustes.isModoOscuro())
            holder.imgMostrar.setImageResource(R.drawable.icono_mostrar_claro);
    }

    @Override
    public int getItemCount() {
        return carpetas.size();
    }

    public interface OnCarpetaClick {
        void onClick(int indice);
    }
}
