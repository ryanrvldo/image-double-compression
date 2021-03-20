package dev.ryanrvldo.imagedoublecompression.compression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.ryanrvldo.imagedoublecompression.databinding.CompressionFragmentBinding

class CompressionFragment : Fragment() {

    private var _binding: CompressionFragmentBinding? = null
    private val binding: CompressionFragmentBinding get() = _binding!!

    private val viewModel: CompressionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CompressionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
