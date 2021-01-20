package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity.MODE_PAINT
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment
import iamutkarshtiwari.github.io.ananas.editimage.fragment.MainMenuFragment
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3
import iamutkarshtiwari.github.io.ananas.editimage.viewmodel.PaintViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PaintFragment : BaseEditFragment(), View.OnClickListener {

    private var imageEraser: ImageView? = null
    private var imageBrush: ImageView? = null

    private val compositeDisposable = CompositeDisposable()

    private val viewModel: PaintViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_paint, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.back_to_main).setOnClickListener(this)

        val eraserView = view.findViewById<LinearLayout>(R.id.eraser_btn)
        eraserView.setOnClickListener(this)
        imageEraser = eraserView.findViewById(R.id.eraser_icon)

        val brushView = view.findViewById<LinearLayout>(R.id.brush_btn)
        brushView.setOnClickListener(this)
        imageBrush = brushView.findViewById(R.id.brush_icon)

        view.findViewById<View>(R.id.settings).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.back_to_main) {
            backToMain()
        } else if (view.id == R.id.eraser_btn) {
            if (viewModel.isEraser.value == false) {
                viewModel.setIsEraser(true)
                toggleButtons()
            }
        } else if (view.id == R.id.brush_btn) {
            if (viewModel.isEraser.value == true) {
                viewModel.setIsEraser(false)
                toggleButtons()
            }
        } else if (view.id == R.id.settings) {

            if (viewModel.isEraser.value == true) {
                showBottomSheet(EraserConfigDialog())
            } else {
                showBottomSheet(BrushConfigDialog())
            }
        }
    }

    private fun showBottomSheet(dialogFragment: BottomSheetDialogFragment) {
        if (dialogFragment.isAdded) return
        dialogFragment.show(parentFragmentManager, dialogFragment.tag)
    }

    override fun backToMain() {
        activityInstance?.apply {
            bottomGallery.currentItem = MainMenuFragment.INDEX
            mainImage.visibility = VISIBLE
            paintView.reset()
            paintView.visibility = View.GONE
            mode = EditImageActivity.MODE_NONE
            bannerFlipper.showPrevious()
        }


        viewModel.resetToDefault()
    }

    override fun onShow() {
        activityInstance?.apply {
            mode = MODE_PAINT
            mainImage.setImageBitmap(mainBit)
            paintView.visibility = VISIBLE
            bannerFlipper.showNext()
        }

    }

    private fun toggleButtons() {
        imageEraser?.setImageResource(
                if (viewModel.isEraser.value == true) R.drawable.ic_eraser_enabled
                else R.drawable.ic_eraser_disabled)
        imageBrush?.setImageResource(
                if (viewModel.isEraser.value == true) R.drawable.ic_brush_grey_24dp
                else R.drawable.ic_brush_white_24dp)
    }

    fun savePaintImage() {
        compositeDisposable.clear()
        activityInstance?.apply {
            val applyPaintDisposable = applyPaint(mainBit)
                    .flatMap { bitmap: Bitmap? ->
                        if (bitmap == null) {
                            return@flatMap Single.error<Bitmap>(Throwable("Error occurred while applying paint"))
                        } else {
                            return@flatMap Single.just(bitmap)
                        }
                    }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { activityInstance?.showLoadingDialog() }
                    .doFinally { activityInstance?.dismissLoadingDialog() }
                    .subscribe { bitmap: Bitmap? ->
                        activityInstance?.paintView?.reset()
                        activityInstance?.changeMainBitmap(bitmap, true)
                        backToMain()
                    }
            compositeDisposable.add(applyPaintDisposable)
        }
    }

    private fun applyPaint(mainBitmap: Bitmap): Single<Bitmap?> {
        return Single.fromCallable {
            var resultBit: Bitmap? = null
            activityInstance?.apply {
                val touchMatrix: Matrix? = this.mainImage?.imageViewMatrix
                resultBit = Bitmap.createBitmap(mainBitmap).copy(Bitmap.Config.ARGB_8888, true)
                resultBit?.apply {
                    val canvas = Canvas(this)
                    val data = FloatArray(9)
                    touchMatrix?.getValues(data)
                    val cal = Matrix3(data)
                    val inverseMatrix = cal.inverseMatrix()
                    val matrix = Matrix()
                    matrix.setValues(inverseMatrix.values)
                    handleImage(canvas, matrix)
                }
            }
            resultBit
        }
    }

    private fun handleImage(canvas: Canvas, matrix: Matrix) {
        val f = FloatArray(9)
        matrix.getValues(f)
        val dx = f[Matrix.MTRANS_X].toInt()
        val dy = f[Matrix.MTRANS_Y].toInt()
        val scale_x = f[Matrix.MSCALE_X]
        val scale_y = f[Matrix.MSCALE_Y]
        canvas.save()
        canvas.translate(dx.toFloat(), dy.toFloat())
        canvas.scale(scale_x, scale_y)
        activityInstance?.paintView?.paintBit?.apply {
            canvas.drawBitmap(this, 0f, 0f, null)
        }
        canvas.restore()
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        const val INDEX = ModuleConfig.INDEX_PAINT
        val TAG = PaintFragment::class.java.name

        @JvmStatic
        fun newInstance(): PaintFragment {
            return PaintFragment()
        }
    }
}