package dev.ryanrvldo.imagedoublecompression.core.di

import dev.ryanrvldo.imagedoublecompression.core.service.BoldiVignaCodesService
import dev.ryanrvldo.imagedoublecompression.core.service.GoldbachCodesService
import dev.ryanrvldo.imagedoublecompression.core.util.BinaryConverter
import kotlinx.coroutines.CoroutineScope

object Injection {

    fun provideBinaryConverter() = BinaryConverter()

    fun provideBoldiVignaService(scope: CoroutineScope) = BoldiVignaCodesService(
        scope,
        provideBinaryConverter()
    )

    fun provideGoldbachService(scope: CoroutineScope) = GoldbachCodesService(
        scope,
        provideBinaryConverter()
    )

}
