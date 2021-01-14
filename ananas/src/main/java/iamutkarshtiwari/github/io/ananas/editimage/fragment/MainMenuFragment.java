package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.crop.CropFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.paint.PaintFragment;


public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    public static final String TAG = MainMenuFragment.class.getName();

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_main_menu, null);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle intentBundle = getArguments();

        View stickerBtn = view.findViewById(R.id.btn_stickers);
        View filterButton = view.findViewById(R.id.btn_filter);
        View cropBtn = view.findViewById(R.id.btn_crop);
        View rotateBtn = view.findViewById(R.id.btn_rotate);
        View mTextBtn = view.findViewById(R.id.btn_text);
        View mPaintBtn = view.findViewById(R.id.btn_paint);
        View mBeautyBtn = view.findViewById(R.id.btn_beauty);
        View mBrightnessBtn = view.findViewById(R.id.btn_brightness);
        View mSaturationBtn = view.findViewById(R.id.btn_contrast);

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.STICKER_FEATURE, false)) {
            stickerBtn.setVisibility(View.VISIBLE);
            stickerBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.FILTER_FEATURE, false)) {
            filterButton.setVisibility(View.VISIBLE);
            filterButton.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.CROP_FEATURE, false)) {
            cropBtn.setVisibility(View.VISIBLE);
            cropBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ROTATE_FEATURE, false)) {
            rotateBtn.setVisibility(View.VISIBLE);
            rotateBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ADD_TEXT_FEATURE, false)) {
            mTextBtn.setVisibility(View.VISIBLE);
            mTextBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.PAINT_FEATURE, false)) {
            mPaintBtn.setVisibility(View.VISIBLE);
            mPaintBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BEAUTY_FEATURE, false)) {
            mBeautyBtn.setVisibility(View.VISIBLE);
            mBeautyBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BRIGHTNESS_FEATURE, false)) {
            mBrightnessBtn.setVisibility(View.VISIBLE);
            mBrightnessBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.SATURATION_FEATURE, false)) {
            mSaturationBtn.setVisibility(View.VISIBLE);
            mSaturationBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_stickers) {
            onStickClick();
        } else if (v.getId() == R.id.btn_filter) {
            onFilterClick();
        } else if (v.getId() == R.id.btn_crop) {
            onCropClick();
        } else if (v.getId() == R.id.btn_rotate) {
            onRotateClick();
        } else if (v.getId() ==R.id.btn_text) {
            onAddTextClick();
        } else if (v.getId() == R.id.btn_paint) {
            onPaintClick();
        } else if (v.getId() == R.id.btn_beauty) {
            onBeautyClick();
        } else if (v.getId() == R.id.btn_brightness) {
            onBrightnessClick();
        } else if (v.getId() == R.id.btn_contrast) {
            onContrastClick();
        }
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {
        //do nothing
    }

    private void onStickClick() {
        activity.bottomGallery.setCurrentItem(StickerFragment.INDEX);
        activity.stickerFragment.onShow();
    }

    private void onFilterClick() {
        activity.bottomGallery.setCurrentItem(FilterListFragment.INDEX);
        activity.filterListFragment.onShow();
    }

    private void onCropClick() {
        activity.bottomGallery.setCurrentItem(CropFragment.INDEX);
        activity.cropFragment.onShow();
    }

    private void onRotateClick() {
        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
        activity.rotateFragment.onShow();
    }


    private void onAddTextClick() {
        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.addTextFragment.onShow();
    }

    private void onPaintClick() {
        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.paintFragment.onShow();
    }

    private void onBeautyClick() {
        activity.bottomGallery.setCurrentItem(BeautyFragment.INDEX);
        activity.beautyFragment.onShow();
    }

    private void onBrightnessClick() {
        activity.bottomGallery.setCurrentItem(BrightnessFragment.INDEX);
        activity.brightnessFragment.onShow();
    }

    private void onContrastClick() {
        activity.bottomGallery.setCurrentItem(SaturationFragment.INDEX);
        activity.saturationFragment.onShow();
    }
}
