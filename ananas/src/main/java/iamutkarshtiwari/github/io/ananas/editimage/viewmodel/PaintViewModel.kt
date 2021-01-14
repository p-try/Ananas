package iamutkarshtiwari.github.io.ananas.editimage.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaintViewModel : ViewModel() {

    private val mutableBrushSize = MutableLiveData(INITIAL_WIDTH)
    val brushSize: LiveData<Int> get() = mutableBrushSize

    private val mutableBrushColor = MutableLiveData(DEFAULT_COLOR)
    val brushColor: LiveData<Int> get() = mutableBrushColor

    private val mutableBrushOpacity = MutableLiveData(MAX_ALPHA)
    val brushOpacity: LiveData<Float> get() = mutableBrushOpacity

    private val mutableEraserSize = MutableLiveData(INITIAL_WIDTH)
    val eraserSize: LiveData<Int> get() = mutableEraserSize

    private val mutableIsEraser = MutableLiveData(false)
    val isEraser: LiveData<Boolean> get() = mutableIsEraser

    fun setBrushColor(color: Int) {
        mutableBrushColor.value = color
    }

    fun setBrushOpacity(opacity: Float) {
        mutableBrushOpacity.value = opacity / MAX_PERCENT * MAX_ALPHA
    }

    fun setBrushSize(size: Int) {
        mutableBrushSize.value = size
    }

    fun setEraserSize(size: Int) {
        mutableEraserSize.value = size
    }

    fun setIsEraser(isEraser: Boolean) {
        mutableIsEraser.value = isEraser
    }

    fun resetToDefault() {
        mutableBrushSize.value = INITIAL_WIDTH
        mutableBrushColor.value = DEFAULT_COLOR
        mutableBrushOpacity.value = MAX_ALPHA

        mutableEraserSize.value = INITIAL_WIDTH
        mutableIsEraser.value = false
    }

    companion object {
        const val MAX_PERCENT = 100f
        private const val INITIAL_WIDTH = 50
        const val MAX_ALPHA = 255f
        private const val DEFAULT_COLOR = Color.WHITE
    }
}