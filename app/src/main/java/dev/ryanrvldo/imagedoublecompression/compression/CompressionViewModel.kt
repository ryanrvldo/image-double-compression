package dev.ryanrvldo.imagedoublecompression.compression

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ryanrvldo.imagedoublecompression.core.service.BoldiVignaCodesService
import dev.ryanrvldo.imagedoublecompression.core.service.GoldbachCodesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class CompressionViewModel(
    private val boldiVignaService: BoldiVignaCodesService,
    private val goldbachService: GoldbachCodesService,
) : ViewModel() {

    private val _initBytes = MutableLiveData<Array<Byte>>()
    val initBytes: LiveData<Array<Byte>>
        get() = _initBytes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _dictionaryCodes = MutableLiveData<ByteArray>()
    val dictionaryCodes: LiveData<ByteArray>
        get() = _dictionaryCodes

    fun setInitBytes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            _initBytes.postValue(inputStream.readBytes().toTypedArray())
        }
    }

    suspend fun compressImage(init: Array<Byte>): ByteArray {
        val resultDeferred = viewModelScope.async(Dispatchers.Default) {
            _isLoading.postValue(true)
            var resultBytes by Delegates.notNull<ByteArray>()
            runningTime = measureTimeMillis {
                val bvResult: Pair<ByteArray, ByteArray> = boldiVignaService.compress(init)
                _dictionaryCodes.postValue(bvResult.second)

                val gbResult: Pair<ByteArray, ByteArray> =
                    goldbachService.compress(bvResult.first.toTypedArray())
                _dictionaryCodes.postValue(gbResult.second)
                resultBytes = gbResult.first
                _isLoading.postValue(false)
            }.toDouble() / 1000
            calculateCompression(init.size, resultBytes.size)
            resultBytes
        }
        return resultDeferred.await()
    }

    private fun calculateCompression(initSize: Int, resultSize: Int) {
        RC = (resultSize.toDouble() / initSize) * 100
        CR = initSize.toDouble() / resultSize
        SS = (1 - (resultSize.toDouble() / initSize)) * 100
        BR = (resultSize.toDouble() * 8) / initSize
    }

    fun saveDictionaryCodes(file: File, bytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileWriter = FileWriter(file, true)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(bytes.joinToString(separator = ","))
            bufferedWriter.newLine()
            bufferedWriter.flush()
            bufferedWriter.close()
            fileWriter.close()
        }
    }

    companion object {
        var runningTime: Double by Delegates.notNull()
        var RC: Double by Delegates.notNull()
        var CR: Double by Delegates.notNull()
        var SS: Double by Delegates.notNull()
        var BR: Double by Delegates.notNull()
    }

}
