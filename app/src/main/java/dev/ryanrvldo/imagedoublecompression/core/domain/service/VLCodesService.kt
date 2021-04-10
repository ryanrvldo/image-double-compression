package dev.ryanrvldo.imagedoublecompression.core.domain.service

import dev.ryanrvldo.imagedoublecompression.core.util.BinaryConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

abstract class VLCodesService(private val binaryConverter: BinaryConverter) {

    suspend fun compress(init: Array<Byte>): Pair<ByteArray, ByteArray> =
        withContext(Dispatchers.Default) {
            if (init.isEmpty()) return@withContext Pair(byteArrayOf(), byteArrayOf())

            val sortedAndGroupedBytes = init.groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { (_, value) ->
                    value
                }
            val codes: List<String> = getCodeList(sortedAndGroupedBytes.size).toList()
            val byteCodes = sortedAndGroupedBytes.mapIndexed { index, pair ->
                pair.first to codes[index]
            }.toMap()

            val dictionaryCodes = byteCodes.keys.toByteArray()

            var stringBits = init.mapIndexed { _, byte ->
                byteCodes[byte]
            }.joinToString("")

            stringBits += when (val lastBit = stringBits.length.rem(8)) {
                0 -> "00000001"
                else -> "${"0".repeat(7 - lastBit)}1" + String.format(
                    "%08d",
                    Integer.parseInt((9 - lastBit).toString(2))
                )
            }
            binaryConverter.binToHex(stringBits) to dictionaryCodes
        }

    suspend fun getCodeMap(size: Int): Flow<Pair<String, Int>> = flow {
        if (size == 0) return@flow

        for (i in 1..size) {
            val code = encode(i)
            emit(code to i - 1)
        }
    }

    suspend fun getCodeList(size: Int): Flow<String> = flow {
        if (size == 0) return@flow

        for (i in 1..size) {
            val code = encode(i)
            emit(code)
        }
    }

    abstract suspend fun encode(num: Int): String

}
