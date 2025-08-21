package com.sreeram.tlv;


/**
 * Container for a collection of BER TLV structures
 */
class BerTlvs internal constructor(private val tlvs: List<BerTlv>) {

    val list: List<BerTlv>
        get() = tlvs

    fun find(tag: BerTag): BerTlv? {
        tlvs.forEach { tlv ->
            val found = tlv.find(tag)
            if (found != null) {
                return found
            }
        }
        return null
    }

    fun findAll(tag: BerTag): List<BerTlv> {
        val resultList = mutableListOf<BerTlv>()
        tlvs.forEach { tlv ->
            resultList.addAll(tlv.findAll(tag))
        }
        return resultList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BerTlvs

        return tlvs == other.tlvs
    }

    override fun hashCode(): Int {
        return tlvs.hashCode()
    }

    override fun toString(): String {
        return "BerTlvs(tlvs=$tlvs)"
    }
}