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
import iamutkarshtiwari.github.io.ananas.editimage.view.BrightnessView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;


public class BrightnessFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_BRIGHTNESS;
    public static final String TAG = BrightnessFragment.class.getName();

    private static final int INITIAL_BRIGHTNESS = 0;

    private SeekBar mSeekBar;

    public static BrightnessFragment newInstance() {
        return new BrightnessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_brightness, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSeekBar = view.findViewById(R.id.seekBar);

        View mBackToMenu = view.findViewById(R.id.back_to_main);
        mBackToMenu.setOnClickListener(new BrightnessFragment.BackToMenuClick());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                if (getActivityInstance() != null)
                    getActivityInstance().brightnessView.setBright(value / 10f);
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
            getActivityInstance().mode = EditImageActivity.MODE_BRIGHTNESS;
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            getActivityInstance().mainImage.setVisibility(View.GONE);

            getActivityInstance().brightnessView.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().brightnessView.setVisibility(View.VISIBLE);
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
            getActivityInstance().brightnessView.setVisibility(View.GONE);
            getActivityInstance().bannerFlipper.showPrevious();
            getActivityInstance().brightnessView.setBright(INITIAL_BRIGHTNESS);
        }
    }

    public void applyBrightness() {
        if (mSeekBar.getProgress() == mSeekBar.getMax() / 2) {
            backToMain();
            return;
        }
        if (getActivityInstance() != null) {
            Bitmap bitmap = ((BitmapDrawable) getActivityInstance().brightnessView.getDrawable()).getBitmap();
            getActivityInstance().changeMainBitmap(Utils.brightBitmap(bitmap, getActivityInstance().brightnessView.getBright()), true);
        }
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax() / 2);
    }


    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
