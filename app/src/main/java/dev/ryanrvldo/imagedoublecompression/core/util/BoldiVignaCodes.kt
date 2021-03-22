package dev.ryanrvldo.imagedoublecompression.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

class BoldiVignaCodes(private val coroutineScope: CoroutineScope) : VLCodes() {

    companion object {
        const val zeta = 3
    }

    override suspend fun encode(num: Int): String = withContext(coroutineScope.coroutineContext) {
        val h = determineH(num)
        val unaryCode = getUnaryCode(h + 1)
        val x = num - 2.0.pow(h * zeta)

        val z = (2.0.pow((h + 1) * zeta) - 2.0.pow(h * zeta))
        val s = ceil(log2(z))

        val binary = when {
            x < (2.0.pow(s) - z) -> {
                val bin = String.format("%016d", Integer.parseInt(x.toInt().toString(2)))
                bin.substring(bin.length - (s.toInt() - 1), bin.length)
            }
            else -> {
                val bin = String.format(
                    "%016d",
                    Integer.parseInt((2.0.pow(s) + x - z).toInt().toString(2))
                )
                bin.substring(bin.length - s.toInt(), bin.length)
            }
        }
        unaryCode + binary
    }

    fun determineH(num: Int): Int {
        var h = 0
        var i = 0
        var start = 2.0.pow(h * zeta).toInt()
        var end = 2.0.pow((h + 1) * zeta).toInt() - 1
        var isInRange = num in start..end
        while (!isInRange) {
            i++
            start = 2.0.pow((h + i) * zeta).toInt()
            end = 2.0.pow((h + i + 1) * zeta).toInt() - 1
            isInRange = num in start..end
            if (isInRange) {
                h = i
            }
        }
        return h
    }

    fun getUnaryCode(num: Int): String {
        if (num == 1) {
            return "1"
        }
        return "${"0".repeat(num - 1)}1"
    }

}
