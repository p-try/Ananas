package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.FilterAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.fliter.PhotoProcessing;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FilterListFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_FILTER;
    public static final int NULL_FILTER_INDEX = 0;
    public static final String TAG = FilterListFragment.class.getName();

    private Bitmap filterBitmap;
    private Bitmap currentBitmap;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static FilterListFragment newInstance() {
        return new FilterListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_fliter, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView filterRecyclerView = view.findViewById(R.id.filter_recycler);
        FilterAdapter filterAdapter = new FilterAdapter(this, getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        filterRecyclerView.setLayoutManager(layoutManager);
        filterRecyclerView.setAdapter(filterAdapter);

        View backBtn = view.findViewById(R.id.back_to_main);
        backBtn.setOnClickListener(v -> backToMain());
    }

    @Override
    public void onShow() {
        EditImageActivity activity;
        if ((activity = getActivityInstance())!= null) {
            activity.mode = EditImageActivity.MODE_FILTER;
            activity.filterListFragment.setCurrentBitmap(activity.getMainBit());
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setScaleEnabled(false);
            activity.bannerFlipper.showNext();
        }
    }

    @Override
    public void backToMain() {
        EditImageActivity activity;
        if ((activity = getActivityInstance())!= null) {
            currentBitmap = activity.getMainBit();
            filterBitmap = null;
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mode = EditImageActivity.MODE_NONE;
            activity.bottomGallery.setCurrentItem(0);
            activity.mainImage.setScaleEnabled(true);
            activity.bannerFlipper.showPrevious();
        }
    }

    public void applyFilterImage() {
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null) {
            if (currentBitmap != activity.getMainBit()) {
                activity.changeMainBitmap(filterBitmap, true);
            }
            backToMain();
        }
    }

    @Override
    public void onDestroy() {
        tryRecycleFilterBitmap();
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void tryRecycleFilterBitmap() {
        if (filterBitmap != null && (!filterBitmap.isRecycled())) {
            filterBitmap.recycle();
        }
    }

    public void enableFilter(int filterIndex) {
        EditImageActivity activity;
        if (filterIndex == NULL_FILTER_INDEX && (activity = getActivityInstance()) != null) {
            activity.mainImage.setImageBitmap(activity.getMainBit());
            currentBitmap = activity.getMainBit();
            return;
        }

        Disposable applyFilterDisposable = applyFilter(filterIndex)
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
                .subscribe(
                        this::updatePreviewWithFilter,
                        e -> showSaveErrorToast()
                );

        compositeDisposable.add(applyFilterDisposable);
    }

    private void updatePreviewWithFilter(Bitmap bitmapWithFilter) {
        if (bitmapWithFilter == null) return;

        if (filterBitmap != null && (!filterBitmap.isRecycled())) {
            filterBitmap.recycle();
        }

        filterBitmap = bitmapWithFilter;
        EditImageActivity activity;
        if ((activity = getActivityInstance()) != null)
            activity.mainImage.setImageBitmap(filterBitmap);
        currentBitmap = filterBitmap;
    }

    private void showSaveErrorToast() {
        Toast.makeText(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_save_error, Toast.LENGTH_SHORT).show();
    }

    private Single<Bitmap> applyFilter(int filterIndex) {
        return Single.fromCallable(() -> {
            Bitmap bitmap = null;
            EditImageActivity activity;
            if ((activity = getActivityInstance()) != null) {
                Bitmap srcBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                        Bitmap.Config.RGB_565, true));
                bitmap = PhotoProcessing.filterPhoto(srcBitmap, filterIndex);
            }
            return bitmap;
        });
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }
}
