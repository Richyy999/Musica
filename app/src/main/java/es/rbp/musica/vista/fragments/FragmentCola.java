package es.rbp.musica.vista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.rbp.musica.R;

public class FragmentCola extends Fragment {

    public FragmentCola() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cola, container, false);
        SeekBar seekBar = root.findViewById(R.id.seekbarFragment);
        seekBar.setEnabled(false);
        seekBar.getThumb().setAlpha(0);
        TextView lblNombre = root.findViewById(R.id.lblNombreCancionFragment);
        lblNombre.setSelected(true);
        return root;
    }
}
