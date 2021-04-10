package dev.ryanrvldo.imagedoublecompression.core.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected lateinit var initBytes: Array<Byte>
    protected lateinit var resultBytes: ByteArray
    protected lateinit var fileName: String
    protected lateinit var contentDialog: ContentDialog

    protected val getContentLauncher = registerForActivityResult(GetContent()) { onGetContent(it) }
    protected val createContentLauncher = registerForActivityResult(CreateDocument()) {
        onCreateContent(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentDialog = ContentDialog(requireContext())
        bindView()
        observeLiveData()
    }

    protected abstract fun bindView()
    protected abstract fun observeLiveData()

    protected open fun onGetContent(uri: Uri?) {}

    protected open fun onCreateContent(uri: Uri?) {}

    fun isInitBytesInitialized() = this::initBytes.isInitialized

    protected fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}
