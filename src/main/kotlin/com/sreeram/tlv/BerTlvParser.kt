package com.sreeram.tlv;

/**
 * Parser for BER TLV data structures
 */
class BerTlvParser(private val log: IBerTlvLogger = EMPTY_LOGGER) {

    companion object {
        private val EMPTY_LOGGER = object: IBerTlvLogger {
            override val isDebugEnabled: Boolean
                get() = false
            override fun debug(aFormat: String?, vararg args: Any?) {}
        }
    }

    fun parseConstructed(buf: ByteArray): BerTlv {
        return parseConstructed(buf, 0, buf.size)
    }

    fun parseConstructed(buf: ByteArray, offset: Int, length: Int): BerTlv {
        val result = parseWithResult(0, buf, offset, length)
        return result.tlv
    }

    fun parse(buf: ByteArray): BerTlvs {
        return parse(buf, 0, buf.size)
    }

    fun parse(buf: ByteArray, offset: Int, length: Int): BerTlvs {
        val tlvs = mutableListOf<BerTlv>()
        if (length == 0) return BerTlvs(tlvs)

        var currentOffset = offset
        for (i in 0 until 100) {
            val remainingLength = length - (currentOffset - offset)
            val result = parseWithResult(0, buf, currentOffset, remainingLength)
            tlvs.add(result.tlv)

            if (result.offset >= offset + length) {
                break
            }

            currentOffset = result.offset
        }

        return BerTlvs(tlvs)
    }

    private fun parseWithResult(level: Int, buf: ByteArray, offset: Int, length: Int): ParseResult {
        val levelPadding = createLevelPadding(level)

        if (offset + length > buf.size) {
            throw IllegalStateException(
                "Length is out of the range [offset=$offset, len=$length, array.length=${buf.size}, level=$level]"
            )
        }

        if (log.isDebugEnabled) {
            log.debug(
                "{}parseWithResult(level={}, offset={}, len={}, buf={})",
                levelPadding, level, offset, length, HexUtil.toFormattedHexString(buf, offset, length)
            )
        }

        // tag
        val tagBytesCount = getTagBytesCount(buf, offset)
        val tag = createTag(levelPadding, buf, offset, tagBytesCount)

        if (log.isDebugEnabled) {
            log.debug(
                "{}tag = {}, tagBytesCount={}, tagBuf={}",
                levelPadding, tag, tagBytesCount, HexUtil.toFormattedHexString(buf, offset, tagBytesCount)
            )
        }

        // length
        val lengthBytesCount = getLengthBytesCount(buf, offset + tagBytesCount)
        val valueLength = getDataLength(buf, offset + tagBytesCount)

        if (log.isDebugEnabled) {
            log.debug(
                "{}lenBytesCount = {}, len = {}, lenBuf = {}",
                levelPadding, lengthBytesCount, valueLength,
                HexUtil.toFormattedHexString(buf, offset + tagBytesCount, lengthBytesCount)
            )
        }

        // value
        return if (tag.isConstructed) {
            val list = mutableListOf<BerTlv>()
            addChildren(level, buf, offset + tagBytesCount + lengthBytesCount, levelPadding, lengthBytesCount, valueLength, list)

            val resultOffset = offset + tagBytesCount + lengthBytesCount + valueLength
            if (log.isDebugEnabled) {
                log.debug("{}returning constructed offset = {}", levelPadding, resultOffset)
            }
            ParseResult(BerTlv(tag, list), resultOffset)
        } else {
            // value
            val value = ByteArray(valueLength)
            System.arraycopy(buf, offset + tagBytesCount + lengthBytesCount, value, 0, valueLength)
            val resultOffset = offset + tagBytesCount + lengthBytesCount + valueLength

            if (log.isDebugEnabled) {
                log.debug("{}value = {}", levelPadding, HexUtil.toFormattedHexString(value))
                log.debug("{}returning primitive offset = {}", levelPadding, resultOffset)
            }
            ParseResult(BerTlv(tag, value), resultOffset)
        }
    }

    /**
     * @param level          level for debug
     * @param buf            buffer
     * @param offset         offset (first byte)
     * @param levelPadding   level padding (for debug)
     * @param dataBytesCount data bytes count
     * @param valueLength    length
     * @param list           list to add
     */
    private fun addChildren(
        level: Int,
        buf: ByteArray,
        offset: Int,
        levelPadding: String,
        dataBytesCount: Int,
        valueLength: Int,
        list: MutableList<BerTlv>
    ) {
        var startPosition = offset
        var len = valueLength

        while (startPosition < offset + valueLength) {
            val result = parseWithResult(level + 1, buf, startPosition, len)
            list.add(result.tlv)

            startPosition = result.offset
            len = (offset + valueLength) - startPosition

            if (log.isDebugEnabled) {
                log.debug(
                    "{}level {}: adding {} with offset {}, startPosition={}, dataBytesCount={}, valueLength={}",
                    levelPadding, level, result.tlv.tag, result.offset, startPosition, dataBytesCount, valueLength
                )
            }
        }
    }

    private fun createLevelPadding(level: Int): String {
        if (!log.isDebugEnabled) {
            return ""
        }

        return " ".repeat(level * 4)
    }

    private data class ParseResult(
        val tlv: BerTlv,
        val offset: Int
    )

    private fun createTag(levelPadding: String, buf: ByteArray, offset: Int, length: Int): BerTag {
        if (log.isDebugEnabled) {
            log.debug("{}Creating tag {}...", levelPadding, HexUtil.toFormattedHexString(buf, offset, length))
        }
        return BerTag(buf, offset, length)
    }

    private fun getTagBytesCount(buf: ByteArray, offset: Int): Int {
        return if ((buf[offset].toInt() and 0x1F) == 0x1F) { // see subsequent bytes
            var len = 2
            for (i in (offset + 1) until (offset + 10)) {
                if ((buf[i].toInt() and 0x80) != 0x80) {
                    break
                }
                len++
            }
            len
        } else {
            1
        }
    }

    private fun getDataLength(buf: ByteArray, offset: Int): Int {
        var length = buf[offset].toInt() and 0xff

        if ((length and 0x80) == 0x80) {
            val numberOfBytes = length and 0x7f
            if (numberOfBytes > 3) {
                throw IllegalStateException("At position $offset the len is more then 3 [$numberOfBytes]")
            }

            length = 0
            for (i in (offset + 1) until (offset + 1 + numberOfBytes)) {
                length = length * 0x100 + (buf[i].toInt() and 0xff)
            }
        }
        return length
    }

    private fun getLengthBytesCount(buf: ByteArray, offset: Int): Int {
        val len = buf[offset].toInt() and 0xff
        return if ((len and 0x80) == 0x80) {
            1 + (len and 0x7f)
        } else {
            1
        }
    }
}