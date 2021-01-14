package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.MainMenuFragment;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3;
import iamutkarshtiwari.github.io.ananas.editimage.view.CustomPaintView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PaintFragment extends BaseEditFragment implements View.OnClickListener, BrushConfigDialog.Properties, EraserConfigDialog.Properties {

    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();

    private static final float MAX_PERCENT = 100;
    private static final float MAX_ALPHA = 255;
    private static final float INITIAL_WIDTH = 50;

    private boolean isEraser = false;

    //    private CustomPaintView customPaintView;
    private ImageView imageEraser;
    private ImageView imageBrush;

    private BrushConfigDialog brushConfigDialog;
    private EraserConfigDialog eraserConfigDialog;
//    private Dialog loadingDialog;

    private float brushSize = INITIAL_WIDTH;
    private float eraserSize = INITIAL_WIDTH;
    private float brushAlpha = MAX_ALPHA;
    private int brushColor = Color.WHITE;

//    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static PaintFragment newInstance() {
        return new PaintFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_paint, null);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back_to_main).setOnClickListener(this);

        LinearLayout eraserView = view.findViewById(R.id.eraser_btn);
        eraserView.setOnClickListener(this);
        imageEraser = eraserView.findViewById(R.id.eraser_icon);

        LinearLayout brushView = view.findViewById(R.id.brush_btn);
        brushView.setOnClickListener(this);
        imageBrush = brushView.findViewById(R.id.brush_icon);

        view.findViewById(R.id.settings).setOnClickListener(this);

       /* loadingDialog = BaseActivity.getLoadingDialog(activity, R.string.iamutkarshtiwari_github_io_ananas_loading, false);
        customPaintView = activity.findViewById(R.id.custom_paint_view);*/

        setupOptionsConfig();

        ((PaintToolActionListeners) activity).initPaintView(INITIAL_WIDTH, Color.WHITE, MAX_ALPHA, INITIAL_WIDTH);
//        initStroke();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setupOptionsConfig() {
        brushConfigDialog = new BrushConfigDialog();
        brushConfigDialog.setPropertiesChangeListener(this);

        eraserConfigDialog = new EraserConfigDialog();
        eraserConfigDialog.setPropertiesChangeListener(this);
    }

    /*private void initStroke() {
        customPaintView.setWidth(INITIAL_WIDTH);
        customPaintView.setColor(Color.WHITE);
        customPaintView.setStrokeAlpha(MAX_ALPHA);
        customPaintView.setEraserStrokeWidth(INITIAL_WIDTH);
    }*/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_to_main) {
            backToMain();
        } else if (view.getId() == R.id.eraser_btn) {
            if (!isEraser) {
                toggleButtons();
            }
        } else if (view.getId() == R.id.brush_btn) {
            if (isEraser) {
                toggleButtons();
            }
        } else if (view.getId() == R.id.settings) {
            showDialog(isEraser ? eraserConfigDialog : brushConfigDialog);
        }
    }

    private void showDialog(BottomSheetDialogFragment dialogFragment) {
        String tag = dialogFragment.getTag();

        // Avoid IllegalStateException "Fragment already added"
        if (dialogFragment.isAdded()) return;

        dialogFragment.show(requireFragmentManager(), tag);

        if (isEraser) {
            ((PaintToolActionListeners) activity).updateEraserData(eraserSize);
//            updateEraserSize();
        } else {
            ((PaintToolActionListeners) activity).updateBrushData(brushSize, brushAlpha, brushColor);
//            updateBrushParams();
        }
    }

    /*@Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }*/

    public void backToMain() {
        ((PaintToolActionListeners) activity).handleBackToMain();
        /*customPaintView.reset();
        customPaintView.setVisibility(View.GONE);*/
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();

        ((PaintToolActionListeners) activity).setPaintViewVisibility(VISIBLE);
//        customPaintView.setVisibility(View.VISIBLE);
    }

    private void toggleButtons() {
        isEraser = !isEraser;
        ((PaintToolActionListeners) activity).setIsEraser(isEraser);
//        customPaintView.setEraser(isEraser);
        imageEraser.setImageResource(isEraser ? R.drawable.ic_eraser_enabled : R.drawable.ic_eraser_disabled);
        imageBrush.setImageResource(isEraser ? R.drawable.ic_brush_grey_24dp : R.drawable.ic_brush_white_24dp);
    }

    /*@Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }*/

    @Override
    public void onColorChanged(int colorCode) {
        brushColor = colorCode;
        ((PaintToolActionListeners) activity).updateBrushData(brushSize, brushAlpha, brushColor);
//        updateBrushParams();
    }

    @Override
    public void onOpacityChanged(int opacity) {
        brushAlpha = (opacity / MAX_PERCENT) * MAX_ALPHA;
        ((PaintToolActionListeners) activity).updateBrushData(brushSize, brushAlpha, brushColor);
//        updateBrushParams();
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        if (isEraser) {
            this.eraserSize = brushSize;
            ((PaintToolActionListeners) activity).updateEraserData(eraserSize);
//            updateEraserSize();
        } else {
            this.brushSize = brushSize;
            ((PaintToolActionListeners) activity).updateBrushData(brushSize, brushAlpha, brushColor);
//            updateBrushParams();
        }
    }

    /*private void updateBrushParams() {
        customPaintView.setColor(brushColor);
        customPaintView.setWidth(brushSize);
        customPaintView.setStrokeAlpha(brushAlpha);
    }

    private void updateEraserSize() {
        customPaintView.setEraserStrokeWidth(eraserSize);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        brushConfigDialog = null;
        eraserConfigDialog = null;
    }

    public interface PaintToolActionListeners{
        void initPaintView(float brushSize, int brushColor, float eraserSize, float brushAlpha);
        void updateEraserData(float eraserSize);
        void updateBrushData(float brushSize, float brushAlpha, int brushColor);
        void setIsEraser(boolean isEraser);
        void setPaintViewVisibility(int visibility);
        void resetPaintView();
        void handleBackToMain();
    }
}
