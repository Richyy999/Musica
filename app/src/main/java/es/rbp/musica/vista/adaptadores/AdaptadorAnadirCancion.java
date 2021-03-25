package es.rbp.musica.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.rbp.musica.R;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;

public class AdaptadorAnadirCancion extends RecyclerView.Adapter<AdaptadorAnadirCancion.MyHolder> implements SectionIndexer {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnCancionClick onCancionClick;

        private View circulo;

        private TextView lblNombreCancion;
        private TextView lblArtistaAlbum;

        public MyHolder(@NonNull View itemView, OnCancionClick onCancionClick) {
            super(itemView);
            itemView.setOnClickListener(this);

            this.onCancionClick = onCancionClick;

            this.circulo = itemView.findViewById(R.id.circulo);

            this.lblNombreCancion = itemView.findViewById(R.id.lblNombreCancionAnadirCancion);
            this.lblArtistaAlbum = itemView.findViewById(R.id.lblSubtituloAnadirCancion);
        }

        @Override
        public void onClick(View v) {
            onCancionClick.onCancionClick(getAdapterPosition());
            circulo.setSelected(!circulo.isSelected());
        }
    }

    private List<Cancion> canciones;

    private List<Integer> sectionPosition;

    private OnCancionClick onCancionClick;

    public AdaptadorAnadirCancion(List<Cancion> canciones, OnCancionClick onCancionClick) {
        this.canciones = canciones;
        this.onCancionClick = onCancionClick;

        this.sectionPosition = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdaptadorAnadirCancion.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_anadir_canciones, parent, false);
        return new MyHolder(v, onCancionClick);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAnadirCancion.MyHolder holder, int position) {
        Cancion cancion = canciones.get(position);
        Ajustes ajustes = Ajustes.getInstance(null);

        if (ajustes.isUtilizarNombreDeArchivo())
            holder.lblNombreCancion.setText(cancion.getNombreArchivo());
        else
            holder.lblNombreCancion.setText(cancion.getNombre());

        holder.lblNombreCancion.setSelected(true);

        String artista = cancion.getArtista();
        if (artista.equals(Cancion.UNKNOWN))
            artista = Cancion.ARTISTA_DESCONOCIDO;

        String album = cancion.getAlbum();
        if (album.equals(cancion.getCarpetaPadre().substring(cancion.getCarpetaPadre().lastIndexOf("/") + 1)))
            album = Cancion.ALBUM_DESCONOCIDO;

        String textoArtistas = artista + " | " + album;
        holder.lblArtistaAlbum.setText(textoArtistas);
        holder.lblArtistaAlbum.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    @Override
    public Object[] getSections() {
        Ajustes ajustes = Ajustes.getInstance(null);
        List<String> sections = new ArrayList<>();
        for (int i = 0; i < canciones.size(); i++) {
            String section;
            if (ajustes.isUtilizarNombreDeArchivo())
                section = Character.toString(canciones.get(i).getNombreArchivo().charAt(0));
            else
                section = Character.toString(canciones.get(i).getNombre().charAt(0));

            section = section.toUpperCase();

            if (!sections.contains(section)) {
                sections.add(section);
                sectionPosition.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return sectionPosition.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public interface OnCancionClick {
        void onCancionClick(int indice);
    }
}
