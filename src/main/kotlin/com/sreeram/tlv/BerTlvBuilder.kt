package com.sreeram.tlv

import java.math.BigDecimal
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


/**
 * Builder for creating BER TLV structures
 */
class BerTlvBuilder {

    companion object {
        private val ASCII = Charset.forName("US-ASCII")
        private val HUNDRED = BigDecimal(100)
        private const val DEFAULT_SIZE = 5 * 1024

        fun from(tlv: BerTlv): BerTlvBuilder {
            return if (tlv.isConstructed) {
                val builder = template(tlv.tag)
                tlv.values.forEach { nestedTlv ->
                    builder.addBerTlv(nestedTlv)
                }
                builder
            } else {
                BerTlvBuilder().addBerTlv(tlv)
            }
        }

        fun template(template: BerTag): BerTlvBuilder {
            return BerTlvBuilder(template)
        }
    }

    private val bufferOffset: Int
    private var pos: Int
    private val buffer: ByteArray
    private val template: BerTag?

    constructor() : this(null as BerTag?)

    constructor(template: BerTag?) : this(template, ByteArray(DEFAULT_SIZE), 0, DEFAULT_SIZE)

    constructor(tlvs: BerTlvs) : this(null as BerTag?) {
        tlvs.list.forEach { tlv ->
            addBerTlv(tlv)
        }
    }

    constructor(template: BerTag?, buffer: ByteArray, offset: Int, length: Int) {
        this.template = template
        this.buffer = buffer
        this.pos = offset
        this.bufferOffset = offset
    }

    fun addEmpty(tag: BerTag): BerTlvBuilder {
        return addBytes(tag, ByteArray(0), 0, 0)
    }

    fun addByte(tag: BerTag, byte: Byte): BerTlvBuilder {
        // type
        val len = tag.bytes.size
        System.arraycopy(tag.bytes, 0, buffer, pos, len)
        pos += len

        // len
        buffer[pos++] = 1

        // value
        buffer[pos++] = byte
        return this
    }

    fun addAmount(tag: BerTag, amount: BigDecimal): BerTlvBuilder {
        val numeric = amount.multiply(HUNDRED)
        val sb = StringBuilder(12)
        sb.append(numeric.toLong())
        while (sb.length < 12) {
            sb.insert(0, '0')
        }
        return addHex(tag, sb.toString())
    }

    fun addDate(tag: BerTag, date: Date): BerTlvBuilder {
        val format = SimpleDateFormat("yyMMdd")
        return addHex(tag, format.format(date))
    }

    fun addTime(tag: BerTag, date: Date): BerTlvBuilder {
        val format = SimpleDateFormat("HHmmss")
        return addHex(tag, format.format(date))
    }

    fun build(): Int {
        template?.let { tmpl ->
            val tagLen = tmpl.bytes.size
            val lengthBytesCount = calculateBytesCountForLength(pos)

            // shifts array
            System.arraycopy(buffer, bufferOffset, buffer, tagLen + lengthBytesCount, pos)

            // copies tag
            System.arraycopy(tmpl.bytes, 0, buffer, bufferOffset, tmpl.bytes.size)

            fillLength(buffer, tagLen, pos)

            pos += tagLen + lengthBytesCount
        }
        return pos
    }

    private fun fillLength(buffer: ByteArray, offset: Int, length: Int) {
        when {
            length < 0x80 -> {
                buffer[offset] = length.toByte()
            }
            length < 0x100 -> {
                buffer[offset] = 0x81.toByte()
                buffer[offset + 1] = length.toByte()
            }
            length < 0x10000 -> {
                buffer[offset] = 0x82.toByte()
                buffer[offset + 1] = (length / 0x100).toByte()
                buffer[offset + 2] = (length % 0x100).toByte()
            }
            length < 0x1000000 -> {
                buffer[offset] = 0x83.toByte()
                buffer[offset + 1] = (length / 0x10000).toByte()
                buffer[offset + 2] = (length / 0x100).toByte()
                buffer[offset + 3] = (length % 0x100).toByte()
            }
            else -> {
                throw IllegalStateException("length [$length] out of range (0x1000000)")
            }
        }
    }

    private fun calculateBytesCountForLength(length: Int): Int {
        return when {
            length < 0x80 -> 1
            length < 0x100 -> 2
            length < 0x10000 -> 3
            length < 0x1000000 -> 4
            else -> throw IllegalStateException("length [$length] out of range (0x1000000)")
        }
    }

    fun addHex(tag: BerTag, hex: String): BerTlvBuilder {
        val buffer = HexUtil.parseHex(hex)
        return addBytes(tag, buffer, 0, buffer.size)
    }

    fun addBytes(tag: BerTag, bytes: ByteArray): BerTlvBuilder {
        return addBytes(tag, bytes, 0, bytes.size)
    }

    fun addBytes(tag: BerTag, bytes: ByteArray, from: Int, length: Int): BerTlvBuilder {
        val tagLength = tag.bytes.size
        val lengthBytesCount = calculateBytesCountForLength(length)

        // TAG
        System.arraycopy(tag.bytes, 0, buffer, pos, tagLength)
        pos += tagLength

        // LENGTH
        fillLength(buffer, pos, length)
        pos += lengthBytesCount

        // VALUE
        System.arraycopy(bytes, from, buffer, pos, length)
        pos += length

        return this
    }

    fun add(builder: BerTlvBuilder): BerTlvBuilder {
        val array = builder.buildArray()
        System.arraycopy(array, 0, buffer, pos, array.size)
        pos += array.size
        return this
    }

    fun addBerTlv(tlv: BerTlv): BerTlvBuilder {
        return if (tlv.isConstructed) {
            add(from(tlv))
        } else {
            addBytes(tlv.tag, tlv.bytesValue)
        }
    }

    /**
     * Add ASCII text
     *
     * @param tag   tag
     * @param text  text
     * @return builder
     */
    fun addText(tag: BerTag, text: String): BerTlvBuilder {
        return addText(tag, text, ASCII)
    }

    /**
     * Add text with specified charset
     *
     * @param tag     tag
     * @param text    text
     * @param charset charset
     * @return builder
     */
    fun addText(tag: BerTag, text: String, charset: Charset): BerTlvBuilder {
        val buffer = text.toByteArray(charset)
        return addBytes(tag, buffer, 0, buffer.size)
    }

    fun addIntAsHex(tag: BerTag, code: Int, length: Int): BerTlvBuilder {
        val sb = StringBuilder(length * 2)
        sb.append(code)
        while (sb.length < length * 2) {
            sb.insert(0, '0')
        }
        return addHex(tag, sb.toString())
    }

    fun buildArray(): ByteArray {
        val count = build()
        val buf = ByteArray(count)
        System.arraycopy(buffer, 0, buf, 0, count)
        return buf
    }

    fun buildTlv(): BerTlv {
        val count = build()
        return BerTlvParser().parseConstructed(buffer, bufferOffset, count)
    }

    fun buildTlvs(): BerTlvs {
        val count = build()
        return BerTlvParser().parse(buffer, bufferOffset, count)
    }
}