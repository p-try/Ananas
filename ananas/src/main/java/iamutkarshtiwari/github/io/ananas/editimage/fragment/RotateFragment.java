package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.view.RotateImageView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RotateFragment extends BaseEditFragment implements OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final String TAG = RotateFragment.class.getName();

    private static final int RIGHT_ANGLE = 90;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static RotateFragment newInstance() {
        return new RotateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_rotate, null);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View backToMenu = view.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        ImageView rotateLeft = view.findViewById(R.id.rotate_left);
        rotateLeft.setOnClickListener(this);

        ImageView rotateRight = view.findViewById(R.id.rotate_right);
        rotateRight.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_ROTATE;
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            getActivityInstance().mainImage.setVisibility(View.GONE);

            getActivityInstance().rotatePanel.addBit(getActivityInstance().getMainBit(),
                    getActivityInstance().mainImage.getBitmapRect());

            getActivityInstance().rotatePanel.reset();
            getActivityInstance().rotatePanel.setVisibility(View.VISIBLE);
            getActivityInstance().bannerFlipper.showNext();
        }
    }

    @Override
    public void backToMain() {
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_NONE;
            getActivityInstance().bottomGallery.setCurrentItem(0);
            getActivityInstance().mainImage.setVisibility(View.VISIBLE);
            getActivityInstance().rotatePanel.setVisibility(View.GONE);
            getActivityInstance().bannerFlipper.showPrevious();
        }
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (getActivityInstance() != null) {
            if (id == R.id.rotate_left) {
                int updatedAngle = getActivityInstance().rotatePanel.getRotateAngle() - RIGHT_ANGLE;
                getActivityInstance().rotatePanel.rotateImage(updatedAngle);
            } else if (id == R.id.rotate_right) {
                int updatedAngle = getActivityInstance().rotatePanel.getRotateAngle() + RIGHT_ANGLE;
                getActivityInstance().rotatePanel.rotateImage(updatedAngle);
            }
        }
    }

    public void applyRotateImage() {
        if (getActivityInstance() != null) {
            if (getActivityInstance().rotatePanel.getRotateAngle() == 0
                    || (getActivityInstance().rotatePanel.getRotateAngle() % 360) == 0) {
                backToMain();
            } else {
                compositeDisposable.clear();
                Disposable applyRotationDisposable = applyRotation(getActivityInstance().getMainBit())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(subscriber -> {
                            if (getActivityInstance() != null)
                                getActivityInstance().showLoadingDialog();
                        })
                        .doFinally(() -> {
                            if (getActivityInstance() != null)
                                getActivityInstance().showLoadingDialog();
                        })
                        .subscribe(processedBitmap -> {
                            if (processedBitmap == null)
                                return;

                            applyAndExit(processedBitmap);
                        }, e -> {
                            // Do nothing on error
                        });

                compositeDisposable.add(applyRotationDisposable);
            }
        }
    }

    private Single<Bitmap> applyRotation(Bitmap sourceBitmap) {
        return Single.fromCallable(() -> {
            Bitmap resultBitmap = null;
            if (getActivityInstance() != null) {
                RectF imageRect = getActivityInstance().rotatePanel.getImageNewRect();
                resultBitmap = Bitmap.createBitmap((int) imageRect.width(),
                        (int) imageRect.height(), Bitmap.Config.ARGB_4444);

                Canvas canvas = new Canvas(resultBitmap);
                int w = sourceBitmap.getWidth() >> 1;
                int h = sourceBitmap.getHeight() >> 1;

                float centerX = imageRect.width() / 2;
                float centerY = imageRect.height() / 2;

                float left = centerX - w;
                float top = centerY - h;

                RectF destinationRect = new RectF(left, top, left + sourceBitmap.getWidth(), top
                        + sourceBitmap.getHeight());
                canvas.save();
                canvas.rotate(
                        getActivityInstance().rotatePanel.getRotateAngle(),
                        imageRect.width() / 2,
                        imageRect.height() / 2
                );

                canvas.drawBitmap(
                        sourceBitmap,
                        new Rect(
                                0,
                                0,
                                sourceBitmap.getWidth(),
                                sourceBitmap.getHeight()),
                        destinationRect,
                        null);
                canvas.restore();
            }
            return resultBitmap;
        });
    }

    private void applyAndExit(Bitmap resultBitmap) {
        if (getActivityInstance() != null)
            getActivityInstance().changeMainBitmap(resultBitmap, true);
        backToMain();
    }
}
