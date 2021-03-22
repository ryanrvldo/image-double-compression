package dev.ryanrvldo.imagedoublecompression.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoldbachCodes(private val coroutineScope: CoroutineScope) : VLCodes() {

    private lateinit var primeNumbers: List<Int>

    init {
        coroutineScope.launch {
            primeNumbers = sieveOfEratosthenes(450).toList()
        }
    }

    override suspend fun encode(num: Int): String = withContext(coroutineScope.coroutineContext) {
        val primes = findPrimes(2 * (num + 3))
        val primesWeight: List<Int> = primeNumbers.filter { it <= primes.second }
            .map { prime -> if (prime == primes.first || prime == primes.second) 1 else 0 }
        primesWeight.joinToString(separator = "")
    }

    suspend fun findPrimes(num: Int): Pair<Int, Int> =
        withContext(coroutineScope.coroutineContext) {
            if (num < 8 || num % 2 != 0) throw IllegalArgumentException("Invalid number for: $num.")

            var maxIndex = primeNumbers.lastIndex
            for (i in primeNumbers.indices) {
                if (primeNumbers[i] > num) {
                    maxIndex = i - 1
                    break
                }
            }

            var primes: Pair<Int, Int>? = null
            for (i in 1..maxIndex) {
                if (primes != null) break
                for (j in 0 until i) {
                    val sum = primeNumbers[j] + primeNumbers[i]
                    if (sum == num) {
                        primes = primeNumbers[j] to primeNumbers[i]
                        break
                    }
                }
            }
            primes ?: throw Exception("Primes number is not found.")
        }

    fun sieveOfEratosthenes(n: Int): Flow<Int> = flow {
        val markArr = BooleanArray(n + 1) { true }

        var p = 2
        while (p * p <= n) {
            if (markArr[p]) {
                // Update all multiples of p
                for (i in (p * p)..n step p) {
                    markArr[i] = false
                }
            }
            p++
        }
        // Add all prime numbers
        for (i in 3..n) {
            if (markArr[i]) emit(i)
        }
    }

}
