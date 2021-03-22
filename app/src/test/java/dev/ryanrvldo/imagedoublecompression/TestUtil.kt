package dev.ryanrvldo.imagedoublecompression

import com.google.common.truth.Truth.assertThat

fun assertString(result: String, expected: String) {
    assertThat(result).isNotNull()
    assertThat(result).isNotEmpty()
    assertThat(result).hasLength(expected.length)
    assertThat(result).isEqualTo(expected)
}
