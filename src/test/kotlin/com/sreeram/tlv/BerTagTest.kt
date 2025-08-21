package com.sreeram.tlv

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class BerTagTest {
    @Test
    fun testByteArrayConst() {
        assertEquals(BerTag(0x6f, 0x1c), BerTag(byteArrayOf(0x6f, 0x1c)))
        assertEquals(BerTag(byteArrayOf(0x6f, 0x1c), 0, 2), BerTag(byteArrayOf(0x6f, 0x1c)))
        assertEquals(BerTag(0x6f, 0x1c).hashCode(), BerTag(byteArrayOf(0x6f, 0x1c)).hashCode())
    }

    @Test
    fun testEquals() {
        assertEquals(BerTag(0x01, 0x02, 0x03), BerTag(byteArrayOf(0x01, 0x02, 0x03)))
        assertEquals(BerTag(0x01, 0x02), BerTag(byteArrayOf(0x01, 0x02)))
        assertEquals(BerTag(0x01), BerTag(byteArrayOf(0x01)))

        assertNotEquals(BerTag(0x01), BerTag(byteArrayOf(0x02)))
        assertNotEquals(BerTag(0x01), BerTag(byteArrayOf(0x01, 0x01)))
        assertNotEquals(BerTag(0x01, 0x1), BerTag(byteArrayOf(0x02, 0x1)))
    }

    @Test
    fun testHashcode() {
        assertEquals(BerTag(0x01, 0x02, 0x03).hashCode(), BerTag(byteArrayOf(0x01, 0x02, 0x03)).hashCode())
        assertEquals(BerTag(0x01, 0x02).hashCode(), BerTag(byteArrayOf(0x01, 0x02)).hashCode())
        assertEquals(BerTag(0x01).hashCode(), BerTag(byteArrayOf(0x01)).hashCode())

        assertNotEquals(BerTag(0x01).hashCode(), BerTag(byteArrayOf(0x02)).hashCode())
        assertNotEquals(BerTag(0x01).hashCode(), BerTag(byteArrayOf(0x01, 0x01)).hashCode())
        assertNotEquals(BerTag(0x01, 0x1).hashCode(), BerTag(byteArrayOf(0x02, 0x1)).hashCode())
    }
}