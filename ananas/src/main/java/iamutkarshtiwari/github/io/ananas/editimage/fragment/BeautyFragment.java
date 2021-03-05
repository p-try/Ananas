package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fliter.PhotoProcessing;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BeautyFragment extends BaseEditFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG = BeautyFragment.class.getName();

    public static final int INDEX = ModuleConfig.INDEX_BEAUTY;

    private SeekBar smoothValueBar;
    private SeekBar whiteValueBar;

    private CompositeDisposable disposable = new CompositeDisposable();
    private Disposable beautyDisposable;
    private Bitmap finalBmp;

    private int smooth = 0;
    private int whiteSkin = 0;

    private AppCompatTextView textPercentSmoothness;
    private AppCompatTextView textPercentSkinTone;

    public static BeautyFragment newInstance() {
        return new BeautyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_beauty, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smoothValueBar = view.findViewById(R.id.smooth_value_bar);
        smoothValueBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.LTGRAY,
                PorterDuff.Mode.MULTIPLY));
        whiteValueBar = view.findViewById(R.id.white_skin_value_bar);
        whiteValueBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.LTGRAY,
                PorterDuff.Mode.MULTIPLY));

        textPercentSmoothness = view.findViewById(R.id.textPercentSmoothness);
        textPercentSkinTone = view.findViewById(R.id.textPercentSkinTone);

        View backToMenu = view.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单

        smoothValueBar.setOnSeekBarChangeListener(this);
        whiteValueBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        doBeautyTask();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    protected void doBeautyTask() {
        if (beautyDisposable != null && !beautyDisposable.isDisposed()) {
            beautyDisposable.dispose();
        }
        smooth = smoothValueBar.getProgress();
        whiteSkin = whiteValueBar.getProgress();

        textPercentSkinTone.setText(String.valueOf(whiteSkin));
        textPercentSmoothness.setText(String.valueOf(smooth / 5));

        EditImageActivity activity;
        if (smooth == 0 && whiteSkin == 0 && (activity = getActivityInstance()) != null) {
            activity.mainImage.setImageBitmap(activity.getMainBit());
            return;
        }

        beautyDisposable = beautify(smooth, whiteSkin)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> {
                    EditImageActivity activityInstance;
                    if ((activityInstance = getActivityInstance()) != null)
                        activityInstance.showLoadingDialog();
                })
                .doFinally(() -> {
                    EditImageActivity activityInstance;
                    if ((activityInstance = getActivityInstance()) != null)
                        activityInstance.dismissLoadingDialog();
                })
                .subscribe(bitmap -> {
                    if (bitmap == null)
                        return;
                    EditImageActivity activityInstance;
                    if ((activityInstance = getActivityInstance()) != null)
                        activityInstance.mainImage.setImageBitmap(bitmap);
                    finalBmp = bitmap;
                }, e -> {
                    // Do nothing on error
                });
        disposable.add(beautyDisposable);
    }

    private Single<Bitmap> beautify(int smoothVal, int whiteSkinVal) {
        return Single.fromCallable(() -> {
            Bitmap srcBitmap = null;
            EditImageActivity activity;
            if ((activity = getActivityInstance()) != null) {
                srcBitmap = Bitmap.createBitmap(
                        activity.getMainBit().copy(
                                Bitmap.Config.ARGB_8888, true)
                );
                PhotoProcessing.handleSmoothAndWhiteSkin(srcBitmap, smoothVal, whiteSkinVal);
            }
            return srcBitmap;
        });
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void backToMain() {
        this.smooth = 0;
        this.whiteSkin = 0;
        smoothValueBar.setProgress(0);
        whiteValueBar.setProgress(0);
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_NONE;
            activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
            activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图

            activity.mainImage.setVisibility(View.VISIBLE);
            activity.mainImage.setScaleEnabled(true);
            activity.bannerFlipper.showPrevious();
        }
    }

    @Override
    public void onShow() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_BEAUTY;
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setScaleEnabled(false);
            activity.bannerFlipper.showNext();
        }
    }

    public void applyBeauty() {
        EditImageActivity activity;
        if (finalBmp != null && (smooth != 0 || whiteSkin != 0) && (activity =
                getActivityInstance()) != null) {
            activity.changeMainBitmap(finalBmp, true);
        }

        backToMain();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
