package com.sreeram.tlv

/**
 * Utility class for hexadecimal string conversion
 */
object HexUtil {

    private val CHARS_TABLES = "0123456789ABCDEF".toCharArray()
    val BYTES = ByteArray(128)

    init {
        for (i in 0 until 10) {
            BYTES['0'.code + i] = i.toByte()
            BYTES['A'.code + i] = (10 + i).toByte()
            BYTES['a'.code + i] = (10 + i).toByte()
        }
    }

    fun toHexString(bytes: ByteArray): String {
        return toHexString(bytes, 0, bytes.size)
    }

    fun toFormattedHexString(bytes: ByteArray): String {
        return toFormattedHexString(bytes, 0, bytes.size)
    }

    fun toHexString(bytes: ByteArray, length: Int): String {
        return toHexString(bytes, 0, length)
    }

    fun parseHex(hexString: String): ByteArray {
        val src = hexString.replace("\n", "").replace(" ", "").uppercase().toCharArray()
        val dst = ByteArray(src.size / 2)

        var si = 0
        for (di in dst.indices) {
            val high = BYTES[src[si++].code and 0x7f]
            val low = BYTES[src[si++].code and 0x7f]
            dst[di] = ((high.toInt() shl 4) + low.toInt()).toByte()
        }

        return dst
    }

    fun toFormattedHexString(bytes: ByteArray, offset: Int, length: Int): String {
        val sb = StringBuilder()
        sb.append("[")
        sb.append(length)
        sb.append("] :")

        for (i in 0 until length) {
            val si = offset + i
            val b = bytes[si]

            if (i % 4 == 0) {
                sb.append("  ")
            } else {
                sb.append(' ')
            }

            sb.append(CHARS_TABLES[(b.toInt() and 0xf0) ushr 4])
            sb.append(CHARS_TABLES[b.toInt() and 0x0f])
        }

        return sb.toString()
    }

    fun toHexString(bytes: ByteArray, offset: Int, length: Int): String {
        val dst = CharArray(length * 2)

        var di = 0
        for (i in 0 until length) {
            val si = offset + i
            val b = bytes[si]
            dst[di++] = CHARS_TABLES[(b.toInt() and 0xf0) ushr 4]
            dst[di++] = CHARS_TABLES[b.toInt() and 0x0f]
        }

        return String(dst)
    }
}