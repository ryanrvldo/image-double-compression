package dev.ryanrvldo.imagedoublecompression.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.ryanrvldo.imagedoublecompression.compression.CompressionViewModel
import dev.ryanrvldo.imagedoublecompression.core.di.Injection
import dev.ryanrvldo.imagedoublecompression.core.service.BoldiVignaCodesService
import dev.ryanrvldo.imagedoublecompression.core.service.GoldbachCodesService
import dev.ryanrvldo.imagedoublecompression.decompression.DecompressionViewModel
import kotlinx.coroutines.CoroutineScope

class ViewModelFactory private constructor(
    private val boldiVignaService: BoldiVignaCodesService,
    private val goldbachService: GoldbachCodesService,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(CompressionViewModel::class.java) -> CompressionViewModel(
            boldiVignaService, goldbachService) as T
        modelClass.isAssignableFrom(DecompressionViewModel::class.java) -> DecompressionViewModel(
            boldiVignaService, goldbachService) as T
        else -> throw Throwable("Unknown view model class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(scope: CoroutineScope) =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideBoldiVignaService(scope),
                    Injection.provideGoldbachService(scope)
                )
            }
    }

}
