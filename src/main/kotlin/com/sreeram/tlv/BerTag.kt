package com.sreeram.tlv


class BerTag {
    val bytes: ByteArray

    /**
     * Creates a new tag from given byte array. Similar [BerTag.BerTag] but using
     * the full array.
     *
     * @param aBuf to create the tag
     */
    @JvmOverloads
    constructor(aBuf: ByteArray, aOffset: Int = 0, aLength: Int = aBuf.size) {
        val temp = ByteArray(aLength)
        System.arraycopy(aBuf, aOffset, temp, 0, aLength)
        bytes = temp
    }

    constructor(aFirstByte: Int, aSecondByte: Int) {
        bytes = byteArrayOf((aFirstByte).toByte(), aSecondByte.toByte())
    }

    constructor(aFirstByte: Int, aSecondByte: Int, aFirth: Int) {
        bytes = byteArrayOf((aFirstByte).toByte(), aSecondByte.toByte(), aFirth.toByte())
    }

    constructor(aFirstByte: Int) {
        bytes = byteArrayOf(aFirstByte.toByte())
    }

    val isConstructed: Boolean
        get() = (bytes[0].toInt() and 0x20) != 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val berTag = o as BerTag

        return bytes.contentEquals(berTag.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        return (if (this.isConstructed) "+ " else "- ") + HexUtil.toHexString(bytes, 0, bytes.size)
    }
}
