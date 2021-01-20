package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel;

public class EraserConfigDialog extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private PaintViewModel paintViewModel;

    public EraserConfigDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paintViewModel = new ViewModelProvider(requireActivity()).get(PaintViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eraser_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar eraserSizeSb = view.findViewById(R.id.sbSize);

        if (paintViewModel.getEraserSize().getValue() != null)
            eraserSizeSb.setProgress(paintViewModel.getEraserSize().getValue());

        eraserSizeSb.setOnSeekBarChangeListener(this);

        if (paintViewModel.getEraserSize().getValue() != null) {
            paintViewModel.setEraserSize(paintViewModel.getEraserSize().getValue());
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
        int id = seekBar.getId();
        if (id == R.id.sbSize) {
            paintViewModel.setEraserSize(value);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
