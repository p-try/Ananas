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

import com.canhub.cropper.CropImageView;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        int padding = Utils.dpToPx(8);

        RatioText[] ratioTextList = RatioText.values();
        for (int i = 0; i < ratioTextList.length; i++) {
            EditImageActivity activity;
            if ((activity = getActivityInstance()) != null) {
                TextView text = new TextView(activity);
                toggleButtonStatus(text, false);
                text.setTextSize(15);
                text.setAllCaps(true);
                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                text.setText(getResources().getText(ratioTextList[i].getRatioTextId()));
                text.setPadding(padding, padding, padding, padding);
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
                EditImageActivity activity;
                if ((activity = getActivityInstance()) != null) {
                    Bitmap currentBmp = activity.getMainBit();
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

        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            this.cropPanel = activity.cropPanel;
        }

        View backToMenu = view.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        LinearLayout ratioList = view.findViewById(R.id.ratio_list_group);
        setUpRatioList(ratioList);
    }

    @Override
    public void onShow() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_CROP;
            activity.mainImage.setVisibility(View.GONE);
            cropPanel.setVisibility(View.VISIBLE);
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setScaleEnabled(false);

            activity.bannerFlipper.showNext();
            cropPanel.setImageBitmap(activity.getMainBit());
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
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            activity.mode = EditImageActivity.MODE_NONE;
            cropPanel.setVisibility(View.GONE);
            activity.mainImage.setVisibility(View.VISIBLE);

            activity.mainImage.setScaleEnabled(true);
            activity.bottomGallery.setCurrentItem(0);

            if (selectedTextView != null) {
                selectedTextView.setTextColor(getColorFromRes(selectedTextView.getContext(), UNSELECTED_COLOR));
            }

            activity.bannerFlipper.showPrevious();
        }
    }


    public void applyCropImage() {
        disposables.add(getCroppedBitmap()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> {
                    EditImageActivity activity;
                    if ((activity = getActivityInstance()) != null)
                        activity.showLoadingDialog();
                })
                .doFinally(() -> {
                    EditImageActivity activity;
                    if ((activity = getActivityInstance()) != null)
                        activity.dismissLoadingDialog();
                })
                .subscribe(bitmap -> {
                    EditImageActivity activity;
                    if ((activity = getActivityInstance()) != null)
                        activity.changeMainBitmap(bitmap, true);
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
