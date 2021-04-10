package dev.ryanrvldo.imagedoublecompression

import com.google.common.truth.Truth.assertThat

fun assertString(result: String, expected: String) {
    assertThat(result).isNotNull()
    assertThat(result).isNotEmpty()
    assertThat(result).hasLength(expected.length)
    assertThat(result).isEqualTo(expected)
}

fun setPaddingFlag(stringBit: String): String {
    var result: String = stringBit
    result += when (val lastBit = stringBit.length.rem(8)) {
        0 -> "00000001"
        else -> "${"0".repeat(7 - lastBit)}1" + String.format(
            "%08d",
            Integer.parseInt((9 - lastBit).toString(2))
        )
    }
    return result
}
