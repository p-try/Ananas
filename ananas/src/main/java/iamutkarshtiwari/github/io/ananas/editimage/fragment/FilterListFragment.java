package iamutkarshtiwari.github.io.ananas.editimage.fragment;

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
        if (getActivityInstance() != null) {
            getActivityInstance().mode = EditImageActivity.MODE_FILTER;
            getActivityInstance().filterListFragment.setCurrentBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            getActivityInstance().mainImage.setScaleEnabled(false);
            getActivityInstance().bannerFlipper.showNext();
        }
    }

    @Override
    public void backToMain() {
        if (getActivityInstance() != null) {
            currentBitmap = getActivityInstance().getMainBit();
            filterBitmap = null;
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            getActivityInstance().mode = EditImageActivity.MODE_NONE;
            getActivityInstance().bottomGallery.setCurrentItem(0);
            getActivityInstance().mainImage.setScaleEnabled(true);
            getActivityInstance().bannerFlipper.showPrevious();
        }
    }

    public void applyFilterImage() {
        if (getActivityInstance() != null) {
            if (currentBitmap != getActivityInstance().getMainBit()) {
                getActivityInstance().changeMainBitmap(filterBitmap, true);
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
        if (filterIndex == NULL_FILTER_INDEX && getActivityInstance() != null) {
            getActivityInstance().mainImage.setImageBitmap(getActivityInstance().getMainBit());
            currentBitmap = getActivityInstance().getMainBit();
            return;
        }

        Disposable applyFilterDisposable = applyFilter(filterIndex)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> {
                    if (getActivityInstance() != null)
                        getActivityInstance().showLoadingDialog();
                })
                .doFinally(() -> {
                    if (getActivityInstance() != null)
                        getActivityInstance().dismissLoadingDialog();
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
        if (getActivityInstance() != null)
            getActivityInstance().mainImage.setImageBitmap(filterBitmap);
        currentBitmap = filterBitmap;
    }

    private void showSaveErrorToast() {
        Toast.makeText(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_save_error, Toast.LENGTH_SHORT).show();
    }

    private Single<Bitmap> applyFilter(int filterIndex) {
        return Single.fromCallable(() -> {
            Bitmap bitmap = null;
            if (getActivityInstance() != null) {
                Bitmap srcBitmap = Bitmap.createBitmap(getActivityInstance().getMainBit().copy(
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
