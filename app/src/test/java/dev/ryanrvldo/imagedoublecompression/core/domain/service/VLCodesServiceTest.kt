package dev.ryanrvldo.imagedoublecompression.core.domain.service

import com.google.common.truth.Truth.assertThat
import dev.ryanrvldo.imagedoublecompression.core.di.Injection
import dev.ryanrvldo.imagedoublecompression.setPaddingFlag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
class VLCodesServiceTest {

    private val testScope = TestCoroutineScope()
    private val binaryConverter = Injection.provideBinaryConverter()

    private lateinit var vlCodesService: VLCodesService

    private val dummyString = "GOOD MORNING"

    @Test
    fun `test compress empty bytes with goldbach algorithm return compressed byte and dictionaryCodes`() =
        runBlocking {
            vlCodesService = Injection.provideGoldbachService(testScope)
            val dummyBytes = byteArrayOf().toTypedArray()
            val pairResult = vlCodesService.compress(dummyBytes)
            assertThat(pairResult).isNotNull()

            val expectedCompressedBytes: ByteArray = byteArrayOf()
            assertThat(pairResult.first).isNotNull()
            assertThat(pairResult.first).isEmpty()
            assertThat(pairResult.first).isEqualTo(expectedCompressedBytes)

            val expectedDictionaryBytes: ByteArray = byteArrayOf()
            assertThat(pairResult.second).isNotNull()
            assertThat(pairResult.second).isEmpty()
            assertThat(pairResult.second).isEqualTo(expectedDictionaryBytes)
        }

    @Test
    fun `test compress dummy bytes with goldbach algorithm return compressed byte and dictionaryCodes`() =
        runBlocking {
            vlCodesService = Injection.provideGoldbachService(testScope)
            val dummyBytes = dummyString.toByteArray().toTypedArray()
            val pairResult = vlCodesService.compress(dummyBytes)
            assertThat(pairResult).isNotNull()

            val compressedBinary = setPaddingFlag("10111111001010100111100101011010001011101")
            val expectedCompressedBytes: ByteArray = binaryConverter.binToHex(compressedBinary)
            assertThat(pairResult.first).isNotNull()
            assertThat(pairResult.first).isNotEmpty()
            assertThat(pairResult.first).hasLength(expectedCompressedBytes.size)
            assertThat(pairResult.first).isEqualTo(expectedCompressedBytes)

            val expectedDictionaryBytes: ByteArray = "OGND MRI".toByteArray()
            assertThat(pairResult.second).isNotNull()
            assertThat(pairResult.second).isNotEmpty()
            assertThat(pairResult.second).hasLength(expectedDictionaryBytes.size)
            assertThat(pairResult.second).isEqualTo(expectedDictionaryBytes)
        }

    @Test
    fun `test compress empty bytes with boldi vigna algorithm return compressed byte and dictionaryCodes`() =
        runBlocking {
            vlCodesService = Injection.provideBoldiVignaService(testScope)
            val dummyBytes = byteArrayOf().toTypedArray()
            val pairResult = vlCodesService.compress(dummyBytes)
            assertThat(pairResult).isNotNull()

            val expectedCompressedBytes: ByteArray = byteArrayOf()
            assertThat(pairResult.first).isNotNull()
            assertThat(pairResult.first).isEmpty()
            assertThat(pairResult.first).isEqualTo(expectedCompressedBytes)

            val expectedDictionaryBytes: ByteArray = byteArrayOf()
            assertThat(pairResult.second).isNotNull()
            assertThat(pairResult.second).isEmpty()
            assertThat(pairResult.second).isEqualTo(expectedDictionaryBytes)
        }

    @Test
    fun `test compress dummy bytes with boldi vigna algorithm return compressed byte and dictionaryCodes`() =
        runBlocking {
            vlCodesService = Injection.provideBoldiVignaService(testScope)
            val dummyBytes = "RANDI BACA BUKU".toByteArray().toTypedArray()
            val pairResult = vlCodesService.compress(dummyBytes)
            assertThat(pairResult).isNotNull()
            val compressedBinary =
                setPaddingFlag("110110011101111010000010101011100010000110010101011110001000101100")
            val expectedCompressedBytes: ByteArray = binaryConverter.binToHex(compressedBinary)
            assertThat(pairResult.first).isNotNull()
            assertThat(pairResult.first).isNotEmpty()
            assertThat(pairResult.first).hasLength(expectedCompressedBytes.size)
            assertThat(pairResult.first).isEqualTo(expectedCompressedBytes)

            val expectedDictionaryBytes: ByteArray = "A BURNDICK".toByteArray()
            assertThat(pairResult.second).isNotNull()
            assertThat(pairResult.second).isNotEmpty()
            assertThat(pairResult.second).hasLength(expectedDictionaryBytes.size)
            assertThat(pairResult.second).isEqualTo(expectedDictionaryBytes)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `test decompress empty dictionary bytes with goldbach algorithm throw exception`() =
        runBlocking {
            vlCodesService = Injection.provideGoldbachService(testScope)
            val result = vlCodesService.decompress(byteArrayOf(2, 3, 4, 5), byteArrayOf())
            assertThat(result).isEmpty()
        }

    @Test
    fun `test decompress empty bytes with goldbach algorithm return empty array`() = runBlocking {
        vlCodesService = Injection.provideGoldbachService(testScope)
        val result = vlCodesService.decompress(byteArrayOf(), byteArrayOf(2, 3, 4, 5))
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test decompress dummy bytes with goldbach algorithm return decompressed bytes`() =
        runBlocking {
            vlCodesService = Injection.provideGoldbachService(testScope)
            val dummyBytes = dummyString.toByteArray().toTypedArray()
            val compressedResult = vlCodesService.compress(dummyBytes)
            assertThat(compressedResult).isNotNull()

            val bytesResult =
                vlCodesService.decompress(compressedResult.first, compressedResult.second)
            val decompressedResult = String(bytesResult)
            assertThat(decompressedResult).isNotNull()
            assertThat(decompressedResult).isNotEmpty()
            assertThat(decompressedResult).isEqualTo(dummyString)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `test decompress empty dictionary bytes with boldi vigna algorithm throw exception`() =
        runBlocking {
            vlCodesService = Injection.provideBoldiVignaService(testScope)
            val result = vlCodesService.decompress(byteArrayOf(2, 3, 4, 5), byteArrayOf())
            assertThat(result).isEmpty()
        }

    @Test
    fun `test decompress empty bytes with boldi vigna algorithm return empty array`() =
        runBlocking {
            vlCodesService = Injection.provideBoldiVignaService(testScope)
            val result = vlCodesService.decompress(byteArrayOf(), byteArrayOf(2, 3, 4, 5))
            assertThat(result).isNotNull()
            assertThat(result).isEmpty()
        }

    @Test
    fun `test decompress dummy bytes with boldi vigna algorithm return decompressed bytes`() =
        runBlocking {
            vlCodesService = Injection.provideBoldiVignaService(testScope)
            val dummyBytes = dummyString.toByteArray().toTypedArray()
            val compressedResult = vlCodesService.compress(dummyBytes)
            assertThat(compressedResult).isNotNull()

            val bytesResult =
                vlCodesService.decompress(compressedResult.first, compressedResult.second)
            val decompressedResult = String(bytesResult)
            assertThat(decompressedResult).isNotNull()
            assertThat(decompressedResult).isNotEmpty()
            assertThat(decompressedResult).isEqualTo(dummyString)
        }

}
