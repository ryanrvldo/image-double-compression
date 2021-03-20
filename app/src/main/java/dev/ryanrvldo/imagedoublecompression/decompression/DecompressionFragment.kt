package dev.ryanrvldo.imagedoublecompression.decompression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.ryanrvldo.imagedoublecompression.R

class DecompressionFragment : Fragment() {

    private lateinit var viewModel: DecompressionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.decompression_fragment, container, false)
    }

}
