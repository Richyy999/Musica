package es.rbp.musica.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.entidad.Playlist;

public class AdaptadorSnackbarPlaylist extends RecyclerView.Adapter<AdaptadorSnackbarPlaylist.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnPlaylistClicked onPlaylistClicked;

        private TextView lblNombrePlaylist;

        public MyHolder(@NonNull View itemView, OnPlaylistClicked onPlaylistClicked) {
            super(itemView);

            this.onPlaylistClicked = onPlaylistClicked;

            itemView.setOnClickListener(this);
            lblNombrePlaylist = itemView.findViewById(R.id.lblNombrePlaylist);
        }

        @Override
        public void onClick(View v) {
            onPlaylistClicked.onPlaylistClick(getAdapterPosition());
        }
    }

    private List<Playlist> playlists;

    private OnPlaylistClicked onPlaylistClicked;

    public AdaptadorSnackbarPlaylist(List<Playlist> playlists, OnPlaylistClicked onPlaylistClicked) {
        this.playlists = playlists;
        this.onPlaylistClicked = onPlaylistClicked;
    }

    @NonNull
    @Override
    public AdaptadorSnackbarPlaylist.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_snackbar_playlist, parent, false);
        return new MyHolder(v, onPlaylistClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorSnackbarPlaylist.MyHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        holder.lblNombrePlaylist.setText(playlist.getNombre());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public interface OnPlaylistClicked {
        void onPlaylistClick(int indice);
    }
}
