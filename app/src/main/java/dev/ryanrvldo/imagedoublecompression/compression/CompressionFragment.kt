package dev.ryanrvldo.imagedoublecompression.compression

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.ryanrvldo.imagedoublecompression.R
import dev.ryanrvldo.imagedoublecompression.core.ui.BaseFragment
import dev.ryanrvldo.imagedoublecompression.core.ui.ViewModelFactory
import dev.ryanrvldo.imagedoublecompression.databinding.CompressionFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class CompressionFragment : BaseFragment() {

    private var _binding: CompressionFragmentBinding? = null
    private val binding: CompressionFragmentBinding get() = _binding!!

    private val viewModel: CompressionViewModel by viewModels {
        ViewModelFactory.getInstance(lifecycleScope)
    }

    private lateinit var hash: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CompressionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun bindView() {
        binding.btnClearImage.hide()
        binding.btnClearImage.setOnClickListener { clearImage() }

        binding.btnSelectImage.setOnClickListener { getContentLauncher.launch("image/*") }

        binding.btnCompress.setOnClickListener {
            if (!isInitBytesInitialized() || binding.btnSelectImage.isShown) {
                showToast(getString(R.string.please_init_image))
                return@setOnClickListener
            }
            compressImage()
        }
    }

    override fun observeLiveData() {
        viewModel.initBytes.observe(viewLifecycleOwner) { bytes ->
            this.initBytes = bytes
            binding.tvImgSize.text = String.format("%.2f kB", initBytes.size.toDouble() / 1_000)
            binding.image.setImageBitmap(BitmapFactory.decodeByteArray(
                bytes.toByteArray(),
                0,
                bytes.size)
            )
        }

        viewModel.dictionaryCodes.observe(viewLifecycleOwner) { saveDictionaryCodes(it) }

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
            inputStream?.let { stream -> viewModel.setInitBytes(stream) }
            binding.btnClearImage.show()
            binding.btnSelectImage.hide()
            it.path?.let { path ->
                fileName = path.substringAfterLast("/")
                binding.tvImgFile.text = fileName
            }
        }
    }

    private fun compressImage() {
        lifecycleScope.launch(Dispatchers.Default) {
            hash = UUID.randomUUID().toString()
            resultBytes = viewModel.compressImage(initBytes)
            createContentLauncher.launch("${fileName.substringBefore(".")}-$hash.bc")
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

            val resultMsg = """
                The result of compression process:
                Running Time: ${CompressionViewModel.runningTime} seconds.
                RC: ${String.format("%.2f", CompressionViewModel.RC)}%
                CR: ${String.format("%.4f.", CompressionViewModel.CR)}
                SS: ${String.format("%.4f", CompressionViewModel.SS)}%
                BR: ${String.format("%.4f.", CompressionViewModel.BR)}""".trimIndent()
            binding.tvResultStatus.text = resultMsg
        }
    }

    private fun saveDictionaryCodes(byteArray: ByteArray) {
        if (!this::hash.isInitialized) {
            showToast("Hash not initialized yet.")
            return
        }

        val file = File(
            requireContext().getExternalFilesDir(null),
            "${fileName.substringBefore(".")}-${hash}.dc"
        )
        try {
            viewModel.saveDictionaryCodes(file, byteArray)
        } catch (exception: IOException) {
            Log.e(this@CompressionFragment.javaClass.name, "saveDictionaryCodes: ", exception)
            showToast("There is an error that occurred when saving the dictionary codes file.")
        }
    }

    private fun clearImage() {
        binding.tvImgFile.text = buildString {
            append(getString(R.string.file_desc))
            append(getString(R.string.not_available))
        }
        binding.tvImgSize.text = buildString {
            append(getString(R.string.size_desc))
            append(getString(R.string.not_available))
        }
        binding.image.setImageDrawable(null)
        initBytes = emptyArray()
        binding.btnSelectImage.show()
        binding.btnClearImage.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
