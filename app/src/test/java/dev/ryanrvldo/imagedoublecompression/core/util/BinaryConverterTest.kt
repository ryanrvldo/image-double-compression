package dev.ryanrvldo.imagedoublecompression.core.util

import com.google.common.truth.Truth.assertThat
import dev.ryanrvldo.imagedoublecompression.assertString
import dev.ryanrvldo.imagedoublecompression.core.di.Injection
import org.junit.Test

@ExperimentalUnsignedTypes
class BinaryConverterTest {

    private val dummyString = "GOOD MORNING"
    private val binaryConverter = Injection.provideBinaryConverter()

    @Test
    fun `test convert empty array to binary return empty string`() {
        val dummyBytes = byteArrayOf()
        val result = binaryConverter.hexToBin(dummyBytes)
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test convert array to binary return binary result`() {
        val dummyBytes = dummyString.toByteArray()
        val result = binaryConverter.hexToBin(dummyBytes)
        val expected =
            "010001110100111101001111010001000010000001001101010011110101001001001110010010010100111001000111"
        assertString(result, expected)
    }

    @Test
    fun `test convert empty string to byte array return empty byte`() {
        val result = binaryConverter.binToHex("")
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test convert binary string to byte array return result bytes`() {
        val result: ByteArray =
            binaryConverter.binToHex("010001110100111101001111010001000010000001001101010011110101001001001110010010010100111001000111")
        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        assertThat(result).isEqualTo(dummyString.toByteArray())
    }
}
