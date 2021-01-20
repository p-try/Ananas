package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.ColorPickerAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel;

import static iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel.MAX_ALPHA;
import static iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel.MAX_PERCENT;

public class BrushConfigDialog extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private PaintViewModel paintViewModel;

    public BrushConfigDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paintViewModel = new ViewModelProvider(requireActivity()).get(PaintViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brush_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvColor = view.findViewById(R.id.rvColors);
        SeekBar sbOpacity = view.findViewById(R.id.sbOpacity);
        SeekBar sbBrushSize = view.findViewById(R.id.sbSize);

        if (paintViewModel.getBrushOpacity().getValue() != null)
            sbOpacity.setProgress((int) (paintViewModel.getBrushOpacity().getValue() * MAX_PERCENT / MAX_ALPHA));

        if (paintViewModel.getBrushSize().getValue() != null)
            sbBrushSize.setProgress(paintViewModel.getBrushSize().getValue());

        sbOpacity.setOnSeekBarChangeListener(this);
        sbBrushSize.setOnSeekBarChangeListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManager);
        rvColor.setHasFixedSize(true);

        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(requireActivity());
        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            paintViewModel.setBrushColor(colorCode);
            paintViewModel.setBrushOpacity(sbOpacity.getProgress());
        });
        rvColor.setAdapter(colorPickerAdapter);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
        int id = seekBar.getId();
        if (id == R.id.sbOpacity) {
            paintViewModel.setBrushOpacity(value);
        } else if (id == R.id.sbSize) {
            paintViewModel.setBrushSize(value);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
