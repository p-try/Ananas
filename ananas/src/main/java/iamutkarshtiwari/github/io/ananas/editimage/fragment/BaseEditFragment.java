package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;

public abstract class BaseEditFragment extends Fragment {

    protected EditImageActivity activity;

    private void ensureEditActivity() {
        if (activity == null) {
            activity = (EditImageActivity) getActivity();
        }
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        ensureEditActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        ensureEditActivity();
    }

    public abstract void onShow();

    public abstract void backToMain();

}

