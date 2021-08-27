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
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;

public class AdaptadorCola extends RecyclerView.Adapter<AdaptadorCola.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnCancionColaClick onCancionColaClick;

        private final TextView lblNombreCancion;
        private final TextView lblArtista;

        private final ImageView imgEliminar;

        public MyHolder(@NonNull View itemView, OnCancionColaClick onCancionColaClick) {
            super(itemView);

            this.onCancionColaClick = onCancionColaClick;

            lblNombreCancion = itemView.findViewById(R.id.lblNombreCancionRecyclerCola);
            lblArtista = itemView.findViewById(R.id.lblArtistaCola);

            imgEliminar = itemView.findViewById(R.id.btnEliminarDeCola);
            imgEliminar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == imgEliminar.getId())
                onCancionColaClick.eliminarDeCola(getBindingAdapterPosition());
        }
    }

    private final List<Cancion> canciones;

    private final OnCancionColaClick onCancionColaClick;

    private final Ajustes ajustes;

    private final Cola cola;

    private final Context context;

    public AdaptadorCola(List<Cancion> canciones, OnCancionColaClick onCancionColaClick, Ajustes ajustes, Cola cola, Context context) {
        this.canciones = canciones;
        this.onCancionColaClick = onCancionColaClick;
        this.ajustes = ajustes;
        this.cola = cola;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorCola.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_cola, parent, false);
        return new MyHolder(v, onCancionColaClick);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCola.MyHolder holder, int position) {
        Cancion cancion = canciones.get(position);

        if (cola.getIndice() == position) {
            if (ajustes.isModoOscuro())
                holder.lblNombreCancion.setTextColor(context.getResources().getColor(R.color.textoCancionActualOscuro, null));
            else
                holder.lblNombreCancion.setTextColor(context.getResources().getColor(R.color.textoCancionActualClaro, null));
        }

        if (ajustes.isUtilizarNombreDeArchivo())
            holder.lblNombreCancion.setText(cancion.getNombreArchivo());
        else
            holder.lblNombreCancion.setText(cancion.getNombre());

        String album = cancion.getAlbum();
        String artista = cancion.getArtista();
        String texto = artista + " | " + album;
        holder.lblArtista.setText(texto);
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    public interface OnCancionColaClick {
        void eliminarDeCola(int indice);
    }
}
