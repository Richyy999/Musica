package es.rbp.musica.vista.adaptadores;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;

public class AdaptadorCanciones extends RecyclerView.Adapter<AdaptadorCanciones.MyHolder> {

    private static final String TAG = "ADAPTADOR CANCION";

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView lblNombreCancion, lblArtista, lblNumCancion;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            lblNombreCancion = itemView.findViewById(R.id.lblNombreCancionRecycler);
            lblArtista = itemView.findViewById(R.id.lblArtista);
            lblNumCancion = itemView.findViewById(R.id.lblNumCancion);

            lblNombreCancion.setSelected(true);
            lblArtista.setSelected(true);
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
        Ajustes ajustes = Ajustes.getInstance(null);

        if (ajustes.isUtilizarNombreDeArchivo())
            holder.lblNombreCancion.setText(cancion.getNombreArchivo());
        else
            holder.lblNombreCancion.setText(cancion.getNombre());

        holder.lblNumCancion.setText(String.valueOf(position + 1));

        String artista = cancion.getArtista();
        if (artista.equals(Cancion.UNKNOWN))
            artista = Cancion.ARTISTA_DESCONOCIDO;

        String album = cancion.getAlbum();
        if (album.equals(cancion.getCarpetaPadre().substring(cancion.getCarpetaPadre().lastIndexOf("/") + 1)))
            album = Cancion.ALBUM_DESCONOCIDO;

        Log.d(TAG, "Album real:" + cancion.getAlbum() + ".Carpeta:" + cancion.getCarpetaPadre() + ".Album:" + album + ".");

        String textoArtistas = artista + " | " + album;
        holder.lblArtista.setText(textoArtistas);
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }
}
