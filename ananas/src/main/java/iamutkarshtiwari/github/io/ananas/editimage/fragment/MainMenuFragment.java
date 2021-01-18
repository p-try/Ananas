package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.crop.CropFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.mainmenu.MenuSectionnActions;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.paint.PaintFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;


public class MainMenuFragment extends BaseEditFragment implements
        View.OnClickListener, MenuSectionnActions {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    public static final String TAG = MainMenuFragment.class.getName();

    private final BehaviorSubject<Boolean> menuOptionsClickableSubject = BehaviorSubject.create();
    private final CompositeDisposable disposable = new CompositeDisposable();

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle intentBundle = getArguments();

        View stickerBtn = view.findViewById(R.id.btn_stickers);
        View filterBtn = view.findViewById(R.id.btn_filter);
        View cropBtn = view.findViewById(R.id.btn_crop);
        View rotateBtn = view.findViewById(R.id.btn_rotate);
        View textBtn = view.findViewById(R.id.btn_text);
        View paintBtn = view.findViewById(R.id.btn_paint);
        View beautyBtn = view.findViewById(R.id.btn_beauty);
        View brightnessBtn = view.findViewById(R.id.btn_brightness);
        View saturationBtn = view.findViewById(R.id.btn_contrast);

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.STICKER_FEATURE, false)) {
            stickerBtn.setVisibility(View.VISIBLE);
            stickerBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.FILTER_FEATURE, false)) {
            filterBtn.setVisibility(View.VISIBLE);
            filterBtn.setOnClickListener(this);
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
            textBtn.setVisibility(View.VISIBLE);
            textBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.PAINT_FEATURE, false)) {
            paintBtn.setVisibility(View.VISIBLE);
            paintBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BEAUTY_FEATURE, false)) {
            beautyBtn.setVisibility(View.VISIBLE);
            beautyBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BRIGHTNESS_FEATURE, false)) {
            brightnessBtn.setVisibility(View.VISIBLE);
            brightnessBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.SATURATION_FEATURE, false)) {
            saturationBtn.setVisibility(View.VISIBLE);
            saturationBtn.setOnClickListener(this);
        }

        disposable.add(
                menuOptionsClickableSubject
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(isClickable -> {
                                    stickerBtn.setClickable(isClickable);
                                    filterBtn.setClickable(isClickable);
                                    cropBtn.setClickable(isClickable);
                                    rotateBtn.setClickable(isClickable);
                                    textBtn.setClickable(isClickable);
                                    paintBtn.setClickable(isClickable);
                                    beautyBtn.setClickable(isClickable);
                                    brightnessBtn.setClickable(isClickable);
                                    saturationBtn.setClickable(isClickable);
                                }, error -> { }
                        )
        );
    }

    @Override
    public void setMenuOptionsClickable(boolean isClickable) {
        menuOptionsClickableSubject.onNext(isClickable);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {
        //do nothing
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

    private void onStickClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(StickerFragment.INDEX);
            getActivityInstance().stickerFragment.onShow();
        }
    }

    private void onFilterClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(FilterListFragment.INDEX);
            getActivityInstance().filterListFragment.onShow();
        }
    }

    private void onCropClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(CropFragment.INDEX);
            getActivityInstance().cropFragment.onShow();
        }
    }

    private void onRotateClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(RotateFragment.INDEX);
            getActivityInstance().rotateFragment.onShow();
        }
    }


    private void onAddTextClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(AddTextFragment.INDEX);
            getActivityInstance().addTextFragment.onShow();
        }
    }

    private void onPaintClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(PaintFragment.INDEX);
            getActivityInstance().paintFragment.onShow();
        }
    }

    private void onBeautyClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(BeautyFragment.INDEX);
            getActivityInstance().beautyFragment.onShow();
        }
    }

    private void onBrightnessClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(BrightnessFragment.INDEX);
            getActivityInstance().brightnessFragment.onShow();
        }
    }

    private void onContrastClick() {
        if (getActivityInstance() != null) {
            getActivityInstance().bottomGallery.setCurrentItem(SaturationFragment.INDEX);
            getActivityInstance().saturationFragment.onShow();
        }
    }

    @Override
    public void onDestroyView() {
        disposable.dispose();
        super.onDestroyView();
    }
}
