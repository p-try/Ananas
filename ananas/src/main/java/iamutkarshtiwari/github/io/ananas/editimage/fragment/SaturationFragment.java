package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
import iamutkarshtiwari.github.io.ananas.editimage.view.SaturationView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;

public class SaturationFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_CONTRAST;
    private static final int INITIAL_SATURATION = 100;
    public static final String TAG = SaturationFragment.class.getName();
    private SeekBar mSeekBar;

    public static SaturationFragment newInstance() {
        return new SaturationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_saturation, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View mBackToMenu = view.findViewById(R.id.back_to_main);
        mBackToMenu.setOnClickListener(new SaturationFragment.BackToMenuClick());

        mSeekBar = view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                if (getActivityInstance() != null)
                getActivityInstance().saturationView.setSaturation(value / 10f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    @Override
    public void onShow() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_SATURATION;
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            getActivityInstance().mainImage.setVisibility(View.GONE);

            getActivityInstance().saturationView.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().saturationView.setVisibility(View.VISIBLE);
            getActivityInstance().bannerFlipper.showNext();
        }
        initView();
    }

    @Override
    public void backToMain() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_NONE;
            getActivityInstance().bottomGallery.setCurrentItem(0);
            getActivityInstance().mainImage.setVisibility(View.VISIBLE);
            getActivityInstance().saturationView.setVisibility(View.GONE);
            getActivityInstance().bannerFlipper.showPrevious();
            getActivityInstance().saturationView.setSaturation(INITIAL_SATURATION);
        }
    }

    public void applySaturation() {
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            backToMain();
            return;
        }
        if (getActivityInstance() != null) {
            Bitmap bitmap = ((BitmapDrawable) getActivityInstance().saturationView.getDrawable()).getBitmap();
            getActivityInstance().changeMainBitmap(Utils.saturationBitmap(bitmap,
                    getActivityInstance().saturationView.getSaturation()), true);
        }
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax());
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
