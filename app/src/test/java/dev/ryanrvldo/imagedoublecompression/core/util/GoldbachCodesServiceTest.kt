package dev.ryanrvldo.imagedoublecompression.core.util

import com.google.common.truth.Truth.assertThat
import dev.ryanrvldo.imagedoublecompression.assertString
import dev.ryanrvldo.imagedoublecompression.core.di.Injection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

@ExperimentalCoroutinesApi
class GoldbachCodesServiceTest {

    private val testScope = TestCoroutineScope()

    private val goldbachCodes = Injection.provideGoldbachService(testScope)

    private val expectedCodes = listOf(
        "11", "101", "011", "1001", "0101", "0011", "00101", "010001", "00011", "0010001", "000101",
        "000011", "0000101", "00010001"
    )

    @Test
    fun `test find all primes number smaller than 450`() = runBlocking {
        val resultFlow = goldbachCodes.sieveOfEratosthenes(450)
        assertThat(resultFlow).isNotNull()

        val result = resultFlow.toList()
        assertThat(result).isNotEmpty()
        assertThat(result).isEqualTo(
            listOf(
                3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
                89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
                179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269,
                271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373,
                379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test find two primes number for sum smaller than 8 throws an error`() = runBlocking {
        assertThat(goldbachCodes.findPrimes(2)).isEqualTo(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test find two primes number for sum odd number throws an error`() = runBlocking {
        assertThat(goldbachCodes.findPrimes(5)).isEqualTo(null)
    }

    @Test
    fun `test find two primes number for sum equal to  8 returns 3 and 5`() = runBlocking {
        val result = goldbachCodes.findPrimes(8)
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(3 to 5)
    }

    @Test
    fun `test find two primes number for sum equal to  20 returns 7 and 13`() = runBlocking {
        val result = goldbachCodes.findPrimes(20)
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(7 to 13)
    }

    @Test
    fun `test find two primes number for sum equal to  28 returns 11 and 17`() = runBlocking {
        val result = goldbachCodes.findPrimes(28)
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(11 to 17)
    }


    @Test
    fun `test find two primes number for sum equal to  34 returns 11 and 23`() = runBlocking {
        val result = goldbachCodes.findPrimes(34)
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(11 to 23)
    }

    @Test
    fun `test encode num 1`() = runBlocking {
        val result = goldbachCodes.encode(1)
        assertString(result, "11")
    }

    @Test
    fun `test encode num 4`() = runBlocking {
        val result = goldbachCodes.encode(4)
        assertString(result, "1001")
    }

    @Test
    fun `test encode num 8`() = runBlocking {
        val result = goldbachCodes.encode(8)
        assertString(result, "010001")
    }

    @Test
    fun `test encode num 14`() = runBlocking {
        val result = goldbachCodes.encode(14)
        assertString(result, "00010001")
    }

    @Test
    fun `test get code list with size 0 returns empty list`() = runBlocking {
        val resultFlow: Flow<String> = goldbachCodes.getCodeList(0)
        assertThat(resultFlow).isNotNull()

        val result = resultFlow.toList()
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test get code list with size 14 returns expected list`() = runBlocking {
        val size = 14
        val resultFlow = goldbachCodes.getCodeList(size)
        assertThat(resultFlow).isNotNull()

        val result = resultFlow.toList()
        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        assertThat(result).hasSize(size)
        assertThat(result).isEqualTo(expectedCodes)
    }

    @Test
    fun `test get pair of code list and number with size 0 returns empty map`() = runBlocking {
        val resultFlow: Flow<Pair<String, Int>> = goldbachCodes.getCodeMap(0)
        assertThat(resultFlow).isNotNull()

        val result = mutableMapOf<String, Int>()
        resultFlow.collect {
            result[it.first] = it.second
        }
        assertThat(result).isEmpty()
    }

    @ExperimentalStdlibApi
    @Test
    fun `test get pair of code list and number with size 14 returns expected map`() = runBlocking {
        val size = 14
        val resultFlow: Flow<Pair<String, Int>> = goldbachCodes.getCodeMap(size)
        assertThat(resultFlow).isNotNull()

        val expected = buildMap<String, Int>(size) {
            for (i in 0 until size) {
                put(expectedCodes[i], i)
            }
        }
        val result = mutableMapOf<String, Int>()
        resultFlow.collect { result[it.first] = it.second }
        assertThat(result).isNotEmpty()
        assertThat(result).hasSize(size)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `test compress text and return compressed result`() {

    }
}
