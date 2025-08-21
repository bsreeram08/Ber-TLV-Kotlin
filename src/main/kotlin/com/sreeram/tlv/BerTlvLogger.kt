package com.sreeram.tlv;

/**
 * Logger utility for BER TLV structures
 */
object BerTlvLogger {

    fun log(padding: String, tlvs: BerTlvs, logger: IBerTlvLogger) {
        tlvs.list.forEach { tlv ->
            log(padding, tlv, logger)
        }
    }

    fun log(padding: String, tlv: BerTlv?, logger: IBerTlvLogger) {
        if (tlv == null) {
            logger.debug("{} is null", padding)
            return
        }

        if (tlv.isConstructed) {
            logger.debug("{} [{}]", padding, HexUtil.toHexString(tlv.tag.bytes))
            tlv.values.forEach { child ->
                log("$padding    ", child, logger)
            }
        } else {
            logger.debug("{} [{}] {}", padding, HexUtil.toHexString(tlv.tag.bytes), tlv.hexValue)
        }
    }
}