package dev.ryanrvldo.imagedoublecompression.decompression

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.ryanrvldo.imagedoublecompression.R
import dev.ryanrvldo.imagedoublecompression.core.ui.BaseFragment
import dev.ryanrvldo.imagedoublecompression.core.ui.ViewModelFactory
import dev.ryanrvldo.imagedoublecompression.databinding.DecompressionFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalUnsignedTypes
class DecompressionFragment : BaseFragment() {

    private var _binding: DecompressionFragmentBinding? = null
    private val binding: DecompressionFragmentBinding
        get() = _binding!!

    private val viewModel: DecompressionViewModel by viewModels {
        ViewModelFactory.getInstance(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DecompressionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun bindView() {
        binding.btnSelectImage.setOnClickListener { getContentLauncher.launch("*/*") }
        binding.btnDecompress.setOnClickListener { decompressImage() }
        binding.btnSaveImage.setOnClickListener {
            createContentLauncher.launch("${fileName.substringBefore(".")}.bmp")
        }
        binding.btnResetResult.setOnClickListener { resetResult() }
    }

    override fun observeLiveData() {
        viewModel.initBytes.observe(viewLifecycleOwner) { bytes ->
            this.initBytes = bytes
            binding.tvImgSize.text = String.format("%.2f kB", bytes.size.toDouble() / 1_000)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { status ->
            if (status) {
                contentDialog.showDialog()
            } else {
                contentDialog.closeDialog()
            }
        }
    }

    override fun onGetContent(uri: Uri?) {
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            inputStream?.let(viewModel::setInitBytes)
            binding.btnSelectImage.hide()
            it.path?.let { path ->
                fileName = path.substringAfterLast("/")
                binding.tvImgFile.text = fileName
            }
        }
    }

    override fun onCreateContent(uri: Uri?) {
        uri?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                val outputStream = requireContext().contentResolver.openOutputStream(it)
                outputStream?.apply {
                    write(resultBytes)
                    close()
                }
            }
        }
    }

    private fun decompressImage() {
        lifecycleScope.launch {
            val file = getDictionaryFile() ?: return@launch
            resultBytes = viewModel.decompressImage(initBytes.toByteArray(), file)
            contentDialog.closeDialog()
            binding.resultContainer.visibility = View.VISIBLE
            binding.imgResult.setImageBitmap(BitmapFactory.decodeByteArray(resultBytes,
                0,
                resultBytes.size)
            )
        }
    }

    private fun getDictionaryFile(): File? {
        val file = File(
            requireContext().getExternalFilesDir(null),
            "${fileName.substringBefore(".")}.dc"
        )
        return if (file.exists()) {
            file
        } else {
            showToast(getString(R.string.dictionary_file_not_found))
            null
        }
    }

    private fun resetResult() {
        with(binding) {
            btnSelectImage.show()
            tvImgFile.text = String()
            tvImgSize.text = String()
            initBytes = emptyArray()
            resultContainer.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
