package dev.ryanrvldo.imagedoublecompression.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class VLCodes {

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
