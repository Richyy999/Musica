package es.rbp.musica.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.entidad.Cancion;

public class AdaptadorCanciones extends RecyclerView.Adapter<AdaptadorCanciones.MyHolder> {

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView lblNombreCancion, lblArtista, lblAlbum, lblNumCancion;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            lblNombreCancion = itemView.findViewById(R.id.lblNombreCancionRecycler);
            lblAlbum = itemView.findViewById(R.id.lblAlbum);
            lblArtista = itemView.findViewById(R.id.lblArtista);
            lblNumCancion = itemView.findViewById(R.id.lblNumCancion);
        }
    }

    private List<Cancion> canciones;

    public AdaptadorCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
    }

    @NonNull
    @Override
    public AdaptadorCanciones.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_canciones, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCanciones.MyHolder holder, int position) {
        Cancion cancion = canciones.get(position);

        holder.lblNombreCancion.setText(cancion.getNombre());
        holder.lblNumCancion.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }
}
