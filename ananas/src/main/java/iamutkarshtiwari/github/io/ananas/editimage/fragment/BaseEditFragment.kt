package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import androidx.fragment.app.Fragment;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;

public abstract class BaseEditFragment extends Fragment {

    protected EditImageActivity getActivityInstance() {
        if (getActivity() == null) return null;
        else return (EditImageActivity) getActivity();
    }

    public abstract void onShow();

    public abstract void backToMain();

}

