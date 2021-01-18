package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnLoadingDialogListener;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
    public static final String TAG = CropFragment.class.getName();

    private static int SELECTED_COLOR = R.color.white;
    private static int UNSELECTED_COLOR = R.color.text_color_gray_3;

    private CropImageView cropPanel;

    private CropRationClick cropRatioClick = new CropRationClick();
    private TextView selectedTextView;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static CropFragment newInstance() {
        return new CropFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_crop, null);
    }

    private void setUpRatioList(LinearLayout ratioList) {
        ratioList.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER;
        params.leftMargin = 20;
        params.rightMargin = 20;

        RatioText[] ratioTextList = RatioText.values();
        for (int i = 0; i < ratioTextList.length; i++) {
            if (getActivityInstance() != null) {
                TextView text = new TextView(getActivityInstance());
                toggleButtonStatus(text, false);
                text.setTextSize(15);
                text.setAllCaps(true);
                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                text.setText(getResources().getText(ratioTextList[i].getRatioTextId()));
                ratioList.addView(text, params);

                if (i == 0) {
                    selectedTextView = text;
                }

                text.setTag(ratioTextList[i]);
                text.setOnClickListener(cropRatioClick);
            }
        }
        toggleButtonStatus(selectedTextView, true);
    }


    private final class CropRationClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            toggleButtonStatus(selectedTextView, false);

            TextView currentTextView = (TextView) view;
            toggleButtonStatus(currentTextView, true);

            selectedTextView = currentTextView;

            RatioText ratioText = (RatioText) currentTextView.getTag();
            if (ratioText == RatioText.FREE) {
                cropPanel.setFixedAspectRatio(false);
            } else if (ratioText == RatioText.FIT_IMAGE) {
                if (getActivityInstance() != null) {
                    Bitmap currentBmp = getActivityInstance().getMainBit();
                    cropPanel.setAspectRatio(currentBmp.getWidth(), currentBmp.getHeight());
                }
            } else {
                AspectRatio aspectRatio = ratioText.getAspectRatio();
                cropPanel.setAspectRatio(aspectRatio.getAspectX(), aspectRatio.getAspectY());
            }
        }
    }

    private void toggleButtonStatus(TextView view, boolean isActive) {
        view.setTextColor(getColorFromRes(view.getContext(), (isActive) ? SELECTED_COLOR : UNSELECTED_COLOR));
        view.setTypeface((isActive) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    private int getColorFromRes(Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivityInstance() != null) {
            this.cropPanel = getActivityInstance().cropPanel;
        }

        View backToMenu = view.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        LinearLayout ratioList = view.findViewById(R.id.ratio_list_group);
        setUpRatioList(ratioList);
    }

    @Override
    public void onShow() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_CROP;

            getActivityInstance().mainImage.setVisibility(View.GONE);
            cropPanel.setVisibility(View.VISIBLE);
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            getActivityInstance().mainImage.setScaleEnabled(false);

            getActivityInstance().bannerFlipper.showNext();
            cropPanel.setImageBitmap(getActivityInstance().getMainBit());
            cropPanel.setFixedAspectRatio(false);
        }
    }


    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }


    @Override
    public void backToMain() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_NONE;
            cropPanel.setVisibility(View.GONE);
            getActivityInstance().mainImage.setVisibility(View.VISIBLE);

            getActivityInstance().mainImage.setScaleEnabled(true);
            getActivityInstance().bottomGallery.setCurrentItem(0);

            if (selectedTextView != null) {
                selectedTextView.setTextColor(getColorFromRes(selectedTextView.getContext(), UNSELECTED_COLOR));
            }

            getActivityInstance().bannerFlipper.showPrevious();
        }
    }


    public void applyCropImage() {
        disposables.add(getCroppedBitmap()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> {
                    if (getActivityInstance() != null)
                        getActivityInstance().showLoadingDialog();
                })
                .doFinally(() -> {
                    if (getActivityInstance() != null)
                        getActivityInstance().dismissLoadingDialog();
                })
                .subscribe(bitmap -> {
                    if (getActivityInstance() != null)
                        getActivityInstance().changeMainBitmap(bitmap, true);
                    backToMain();
                }, e -> {
                    e.printStackTrace();
                    backToMain();
                    Toast.makeText(getContext(), "Error while saving image", Toast.LENGTH_SHORT).show();
                }));
    }

    private Single<Bitmap> getCroppedBitmap() {
        return Single.fromCallable(() -> cropPanel.getCroppedImage());
    }

    @Override
    public void onStop() {
        disposables.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }
}
