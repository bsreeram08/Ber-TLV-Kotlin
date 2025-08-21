package com.sreeram.tlv

import java.lang.String
import kotlin.test.Test

class HexUtilTest {
    @Test
    fun testBytes() {
        for (i in 0..<HexUtil.BYTES.size) {
            if (i % (128 / 8) == 0) {
                println()
            }
            print(String.format(", %2d", HexUtil.BYTES[i]))
        }
        println()
        println("HexUtil.BYTES.length = " + HexUtil.BYTES.size)
    }
}