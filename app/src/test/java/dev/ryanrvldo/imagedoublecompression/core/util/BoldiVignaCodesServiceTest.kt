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
class BoldiVignaCodesServiceTest {

    private val testScope = TestCoroutineScope()

    private val boldiVignaCodes = Injection.provideBoldiVignaService(testScope)

    private val expectedCodes = listOf(
        "100", "1010", "1011", "1100", "1101", "1110", "1111", "0100000", "0100001", "0100010",
        "0100011", "0100100", "0100101", "0100110", "0100111", "01010000"
    )

    @Test
    fun `test get unary code for num 1`() {
        val result = boldiVignaCodes.getUnaryCode(1)
        assertString(result, "1")
    }

    @Test
    fun `test get unary code for num 5`() {
        val result = boldiVignaCodes.getUnaryCode(5)
        assertString(result, "00001")
    }

    @Test
    fun `test get unary code for num 10`() {
        val result = boldiVignaCodes.getUnaryCode(10)
        assertString(result, "0000000001")
    }

    @Test
    fun `test determine h`() {
        val result = boldiVignaCodes.determineH(16)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `test encode num 1`() = runBlocking {
        val result = boldiVignaCodes.encode(1)
        assertString(result, "100")
    }

    @Test
    fun `test encode num 4`() = runBlocking {
        val result = boldiVignaCodes.encode(4)
        assertString(result, "1100")
    }

    @Test
    fun `test encode num 8`() = runBlocking {
        val result = boldiVignaCodes.encode(8)
        assertString(result, "0100000")
    }

    @Test
    fun `test encode num 16`() = runBlocking {
        val result = boldiVignaCodes.encode(16)
        assertString(result, "01010000")
    }

    @Test
    fun `test get code list with size 0 returns empty list`() = runBlocking {
        val resultFlow: Flow<String> = boldiVignaCodes.getCodeList(0)
        assertThat(resultFlow).isNotNull()

        val result = resultFlow.toList()
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test get code list with size 16 returns expected list`() = runBlocking {
        val size = 16
        val resultFlow = boldiVignaCodes.getCodeList(size)
        assertThat(resultFlow).isNotNull()

        val result = resultFlow.toList()
        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        assertThat(result).hasSize(size)
        assertThat(result).isEqualTo(expectedCodes)
    }

    @Test
    fun `test get pair of code list and number with size 0 returns empty map`() = runBlocking {
        val resultFlow: Flow<Pair<String, Int>> = boldiVignaCodes.getCodeMap(0)
        assertThat(resultFlow).isNotNull()

        val result = mutableMapOf<String, Int>()
        resultFlow.collect {
            result[it.first] = it.second
        }
        assertThat(result).isEmpty()
    }

    @ExperimentalStdlibApi
    @Test
    fun `test get pair of code list and number with size 16 returns expected map`() = runBlocking {
        val size = 16
        val resultFlow: Flow<Pair<String, Int>> = boldiVignaCodes.getCodeMap(size)
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

}
