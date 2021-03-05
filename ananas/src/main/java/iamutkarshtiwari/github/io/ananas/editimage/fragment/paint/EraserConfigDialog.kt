package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.view.CircleView
import iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel

class EraserConfigDialog : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var paintViewModel: PaintViewModel
    private lateinit var eraserIndicator: CircleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paintViewModel = ViewModelProvider(requireActivity()).get(
            PaintViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eraser_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eraserSizeSb = view.findViewById<SeekBar>(R.id.sbSize)
        eraserIndicator = view.findViewById(R.id.eraserSizeView)

        paintViewModel.eraserSize.value?.also { progress ->
            eraserSizeSb.progress = progress
            eraserIndicator.setCircleRadius(progress.toFloat())
            paintViewModel.setEraserSize(progress)
        }

        eraserSizeSb.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbSize) {
            paintViewModel.setEraserSize(value)
            eraserIndicator.setCircleRadius(value.toFloat())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}