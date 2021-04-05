package es.rbp.musica.vista.adaptadores;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.entidad.Playlist;

public class AdaptadorPlaylists extends RecyclerView.Adapter<AdaptadorPlaylists.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnPlaylistClick onPlaylistClick;

        private LinearLayout contenedor;

        public TextView lblNombre, lblNumCanciones;

        public ImageView imgPlaylist;

        public MyHolder(@NonNull View itemView, OnPlaylistClick onPlaylistClick) {
            super(itemView);
            itemView.setOnClickListener(this);

            this.onPlaylistClick = onPlaylistClick;

            this.contenedor = itemView.findViewById(R.id.contenedorPlaylist);

            this.lblNombre = itemView.findViewById(R.id.lblNombrePlaylist);
            this.lblNumCanciones = itemView.findViewById(R.id.lblNumCancionesPlaylist);

            this.imgPlaylist = itemView.findViewById(R.id.imgPlaylist);
        }

        @Override
        public void onClick(View v) {
            onPlaylistClick.onPlaylistClick(getAdapterPosition(), this);
        }
    }

    private List<Playlist> playlists;

    private OnPlaylistClick onPlaylistClick;

    private Context context;

    private AccesoFichero accesoFichero;

    public AdaptadorPlaylists(List<Playlist> playlists, OnPlaylistClick onPlaylistClick, Context context) {
        this.playlists = playlists;
        this.onPlaylistClick = onPlaylistClick;
        this.context = context;
        this.accesoFichero = AccesoFichero.getInstance(this.context);
    }

    @NonNull
    @Override
    public AdaptadorPlaylists.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_playlists, parent, false);
        return new MyHolder(v, onPlaylistClick);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPlaylists.MyHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.widthRecyclerPlaylist),
                FrameLayout.LayoutParams.WRAP_CONTENT);

        if (position % 2 == 0) {
            params.gravity = Gravity.END;
        } else
            params.gravity = Gravity.START;

        holder.contenedor.setLayoutParams(params);

        holder.lblNombre.setText(playlist.getNombre());
        holder.lblNombre.setSelected(true);

        String texto;
        if (playlist.getCanciones().size() == 1)
            texto = context.getString(R.string.unaCancion);
        else
            texto = playlist.getCanciones().size() + " " + context.getString(R.string.canciones);

        holder.lblNumCanciones.setText(texto);

        File imagenPlaylist = accesoFichero.getImagenPlaylist(playlist);
        if (imagenPlaylist == null)
            Glide.with(context).load(R.drawable.imagen_playlist).into(holder.imgPlaylist);
        else
            Glide.with(context).load(BitmapFactory.decodeFile(imagenPlaylist.getPath())).into(holder.imgPlaylist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public interface OnPlaylistClick {

        void onPlaylistClick(int indice, MyHolder holder);
    }
}
