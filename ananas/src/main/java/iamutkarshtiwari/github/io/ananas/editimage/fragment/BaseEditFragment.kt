package iamutkarshtiwari.github.io.ananas.editimage.fragment

import androidx.fragment.app.Fragment
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity

abstract class BaseEditFragment : Fragment() {
    protected val activityInstance: EditImageActivity?
        get() {
            return activity?.let { instance ->
                if (instance is EditImageActivity) instance
                else null
            }
        }

    abstract fun onShow()
    abstract fun backToMain()
}