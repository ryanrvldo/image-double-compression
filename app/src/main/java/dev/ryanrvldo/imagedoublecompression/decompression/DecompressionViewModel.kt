package dev.ryanrvldo.imagedoublecompression.decompression

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ryanrvldo.imagedoublecompression.core.service.BoldiVignaCodesService
import dev.ryanrvldo.imagedoublecompression.core.service.GoldbachCodesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class DecompressionViewModel(
    private val boldiVignaService: BoldiVignaCodesService,
    private val goldbachService: GoldbachCodesService,
) : ViewModel() {

    private val _initBytes = MutableLiveData<Array<Byte>>()
    val initBytes: LiveData<Array<Byte>>
        get() = _initBytes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun setInitBytes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            _initBytes.postValue(inputStream.readBytes().toTypedArray())
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun decompressImage(init: ByteArray, dictionaryFile: File): ByteArray {
        val resultDeferred = viewModelScope.async(Dispatchers.Default) {
            _isLoading.postValue(true)

            val dictionaryList = dictionaryFile.bufferedReader()
                .readLines()
            if (dictionaryList.size < 2) throw IllegalArgumentException("Invalid dictionary file")

            val bvDictionary: ByteArray = parseDictionaryStringToByteArray(dictionaryList[0])
            val gbDictionary: ByteArray = parseDictionaryStringToByteArray(dictionaryList[1])

            var resultBytes by Delegates.notNull<ByteArray>()
            runningTime = measureTimeMillis {
                val gbResult = goldbachService.decompress(init, gbDictionary)
                resultBytes = boldiVignaService.decompress(gbResult, bvDictionary)
            }.toDouble() / 1_000
            resultBytes
        }
        return resultDeferred.await()
    }

    private fun parseDictionaryStringToByteArray(dictionary: String): ByteArray {
        return dictionary.split(",")
            .map { it.toByte() }
            .toByteArray()
    }

    companion object {
        var runningTime: Double by Delegates.notNull()
    }

}
