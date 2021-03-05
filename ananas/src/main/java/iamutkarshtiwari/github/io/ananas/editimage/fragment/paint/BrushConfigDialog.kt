package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.adapter.ColorPickerAdapter
import iamutkarshtiwari.github.io.ananas.editimage.view.CircleView
import iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel

class BrushConfigDialog : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var paintViewModel: PaintViewModel
    private lateinit var brushSizeIndicator: CircleView
    private lateinit var opacityIndicator: CircleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paintViewModel = ViewModelProvider(requireActivity()).get(PaintViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_brush_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvColor: RecyclerView = view.findViewById(R.id.rvColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.sbSize)

        brushSizeIndicator = view.findViewById(R.id.brushSizeView)
        opacityIndicator = view.findViewById(R.id.opacitySizeView)
        opacityIndicator.setCircleRadius(100f)

        paintViewModel.apply {
            brushSize.value?.also { progress ->
                sbBrushSize.progress = progress
                brushSizeIndicator.setCircleRadius(progress.toFloat())
            }

            brushOpacity.value?.also { opacity ->
                (opacity * PaintViewModel.MAX_PERCENT / PaintViewModel.MAX_ALPHA).toInt().also {
                    sbOpacity.progress = it
                    opacityIndicator.setCircleAlpha(it)
                }
            }

            brushColor.value?.also {
                brushSizeIndicator.setColor(it)
                opacityIndicator.setColor(it)
            }
        }

        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        val layoutManager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.HORIZONTAL, false
        )

        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)

        val previousSelectedColorPos = paintViewModel.selectedColorPos.value
        if (previousSelectedColorPos!! > 0) rvColor.scrollToPosition(previousSelectedColorPos)
        context?.apply {
            val colorPickerAdapter = ColorPickerAdapter(this, previousSelectedColorPos)
            colorPickerAdapter.setOnColorPickerClickListener { colorCode: Int, selectedColorPos: Int ->
                paintViewModel.setBrushColor(colorCode)
                paintViewModel.setSelectedColorPos(selectedColorPos)
                paintViewModel.setBrushOpacity(sbOpacity.progress.toFloat())
                // Set color to brush size and opacity indicator
                brushSizeIndicator.setColor(colorCode)
                opacityIndicator.setColor(colorCode)
            }
            rvColor.adapter = colorPickerAdapter
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbOpacity) {
            paintViewModel.setBrushOpacity(value.toFloat())
            opacityIndicator.setCircleAlpha(value)
        } else if (id == R.id.sbSize) {
            paintViewModel.setBrushSize(value)
            brushSizeIndicator.setCircleRadius(value.toFloat())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}