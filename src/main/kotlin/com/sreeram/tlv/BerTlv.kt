package com.sreeram.tlv

import java.nio.charset.Charset

/**
 * BER TLV (Tag-Length-Value) data structure implementation
 */
class BerTlv {

    companion object {
        private val ASCII = Charset.forName("US-ASCII")
    }

    val tag: BerTag
    private val value: ByteArray?
    private val list: List<BerTlv>?

    /**
     * Creates constructed TLV
     *
     * @param tag   tag
     * @param list  set of nested TLVs
     */
    constructor(tag: BerTag, list: List<BerTlv>) {
        this.tag = tag
        this.list = list
        this.value = null
    }

    /**
     * Creates primitive TLV
     *
     * @param tag   tag
     * @param value value as ByteArray
     */
    constructor(tag: BerTag, value: ByteArray) {
        this.tag = tag
        this.value = value
        this.list = null
    }

    val isPrimitive: Boolean
        get() = !tag.isConstructed

    val isConstructed: Boolean
        get() = tag.isConstructed

    fun isTag(tag: BerTag): Boolean = this.tag == tag

    //
    // find
    //

    fun find(tag: BerTag): BerTlv? {
        if (tag == this.tag) {
            return this
        }

        if (isConstructed) {
            list?.forEach { tlv ->
                val result = tlv.find(tag)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    fun findAll(tag: BerTag): List<BerTlv> {
        val resultList = mutableListOf<BerTlv>()

        if (tag == this.tag) {
            resultList.add(this)
        } else if (isConstructed) {
            list?.forEach { tlv ->
                resultList.addAll(tlv.findAll(tag))
            }
        }

        return resultList
    }

    //
    // getters
    //

    val hexValue: String
        get() {
            if (isConstructed) {
                throw IllegalStateException("Tag is CONSTRUCTED ${HexUtil.toHexString(tag.bytes)}")
            }
            return HexUtil.toHexString(value!!)
        }

    /**
     * Text value with US-ASCII charset
     * @return text
     */
    val textValue: String
        get() = getTextValue(ASCII)

    fun getTextValue(charset: Charset): String {
        if (isConstructed) {
            throw IllegalStateException("TLV is constructed")
        }
        return String(value!!, charset)
    }

    val bytesValue: ByteArray
        get() {
            if (isConstructed) {
                throw IllegalStateException("TLV [$tag] is constructed")
            }
            return value!!
        }

    val intValue: Int
        get() {
            var number = 0

            value!!.forEach { byte ->
                val unsignedByte = if (byte < 0) byte + 256 else byte.toInt()
                number = number * 256 + unsignedByte
            }

            return number
        }

    val values: List<BerTlv>
        get() {
            if (isPrimitive) {
                throw IllegalStateException("Tag is PRIMITIVE")
            }
            return list!!
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BerTlv

        if (tag != other.tag) return false
        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) {
            return false
        }
        return list == other.list
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + (value?.contentHashCode() ?: 0)
        result = 31 * result + (list?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "BerTlv(" +
                "tag=$tag, " +
                "value=${value?.contentToString()}, " +
                "list=$list" +
                ")"
    }
}