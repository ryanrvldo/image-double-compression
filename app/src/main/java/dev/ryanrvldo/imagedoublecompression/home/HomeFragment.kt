package dev.ryanrvldo.imagedoublecompression.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.ryanrvldo.imagedoublecompression.R
import dev.ryanrvldo.imagedoublecompression.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCompression navigate R.id.action_home_to_compression
        binding.btnDecompression navigate R.id.action_home_to_decompression
    }

    private infix fun View.navigate(@IdRes actionId: Int) {
        setOnClickListener { findNavController().navigate(actionId) }
    }

}
