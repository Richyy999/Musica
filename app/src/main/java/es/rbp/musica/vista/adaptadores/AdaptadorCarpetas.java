package es.rbp.musica.vista.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Carpeta;

public class AdaptadorCarpetas extends RecyclerView.Adapter<AdaptadorCarpetas.MyHolder> {

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnCarpetaClicked onCarpetaClicked;

        private TextView lblNombreCarpeta, lblNumCaniones, lblRuta;

        private ImageView btnOcultar;

        private View itemView;

        public MyHolder(@NonNull View itemView, OnCarpetaClicked onCarpetaClicked) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            this.onCarpetaClicked = onCarpetaClicked;

            lblNombreCarpeta = itemView.findViewById(R.id.lblNombreCarpeta);
            lblNumCaniones = itemView.findViewById(R.id.lblNumCanciones);
            lblRuta = itemView.findViewById(R.id.lblRutaCarpeta);

            btnOcultar = itemView.findViewById(R.id.imgOcultar);
            btnOcultar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == itemView.getId())
                onCarpetaClicked.onClick(getAdapterPosition());
            else if (v.getId() == btnOcultar.getId())
                eliminarCanrpeta(getAdapterPosition());
        }
    }

    private List<Carpeta> carpetas;

    private OnCarpetaClicked onCarpetaClicked;

    private Context context;

    public AdaptadorCarpetas(List<Carpeta> carpetas, OnCarpetaClicked onCarpetaClicked, Context context) {
        this.carpetas = carpetas;
        this.onCarpetaClicked = onCarpetaClicked;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_carpetas, parent, false);
        return new MyHolder(v, onCarpetaClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Carpeta carpeta = carpetas.get(position);

        holder.lblNombreCarpeta.setText(carpeta.getNombre());
        holder.lblRuta.setText(carpeta.getRuta());
        holder.lblRuta.setSelected(true);
        if (carpeta.getCanciones().size() == 1)
            holder.lblNumCaniones.setText(R.string.unaCancion);
        else {
            String texto = carpeta.getCanciones().size() + " canciones";
            holder.lblNumCaniones.setText(texto);
        }
    }

    @Override
    public int getItemCount() {
        return carpetas.size();
    }

    private void eliminarCanrpeta(int indice) {
        Ajustes ajustes = Ajustes.getInstance(context);
        Carpeta carpetaEliminada = carpetas.get(indice);
        ajustes.anadirCarpetaOculta(carpetaEliminada.getRuta());
        ajustes.guardarAjustes(context);
        carpetas.remove(indice);
        notifyItemRemoved(indice);
        notifyItemRangeChanged(indice, carpetas.size());
    }

    public interface OnCarpetaClicked {
        void onClick(int indice);
    }
}
