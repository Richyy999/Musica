package es.rbp.musica.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.rbp.musica.R;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnHistorialClicked onHistorialClicked;

        private TextView lblHistorial;

        public MyHolder(@NonNull View itemView, OnHistorialClicked onHistorialClicked) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.onHistorialClicked = onHistorialClicked;

            lblHistorial = itemView.findViewById(R.id.lblTextoHistorial);
        }

        @Override
        public void onClick(View v) {
            onHistorialClicked.onHistorialClick(getAdapterPosition());
        }
    }

    private List<String> historial;

    private OnHistorialClicked onHistorialClicked;

    public AdaptadorHistorial(List<String> historial, OnHistorialClicked onHistorialClicked) {
        this.historial = historial;
        this.onHistorialClicked = onHistorialClicked;
    }

    @NonNull
    @Override
    public AdaptadorHistorial.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_historial, parent, false);
        return new MyHolder(v, onHistorialClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHistorial.MyHolder holder, int position) {
        String texto = historial.get(position);

        holder.lblHistorial.setText(texto);
    }

    @Override
    public int getItemCount() {
        return historial.size();
    }

    public interface OnHistorialClicked {
        void onHistorialClick(int indice);
    }
}
