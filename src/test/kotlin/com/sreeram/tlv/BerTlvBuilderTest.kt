package com.sreeram.tlv

import com.sreeram.tlv.BerTlvBuilder.Companion.template
import com.sreeram.tlv.HexUtil.parseHex
import org.junit.jupiter.api.Assertions.assertArrayEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BerTlvBuilderTest {
     /**
     * 1 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -50 = 56495341
     * 23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -57 = 1000023100000033D44122011003400000481F
     * 23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5A = 1000023100000033
     * 23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F20 = 43415244332F454D562020202020202020202020202020202020
     * 23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F24 = 441231
     * 23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F28 = 0643
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F2A = 0643
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F30 = 0201
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F34 = 06
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -82 = 5C00
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -84 = A0000000031010
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -95 = 4080008000
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9A = 140210
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9B = E800
     * 23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9C = 00
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F02 = 000000030104
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F03 = 000000000000
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F06 = A0000000031010
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F09 = 008C
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F10 = 06010A03A0A100
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1A = 0826
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1C = 3036303435333930
     * 23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1E = 3036303435333930
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F26 = CBFC767977111F15
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F27 = 80
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F33 = E0B8C8
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F34 = 5E0300
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F35 = 22
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F36 = 000E
     * 23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F37 = 461BDA7C
     */
    @Test
    fun test() {
        val hex =  /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
            /*    0 */
            ("50 04 56 49  53 41 57 13    10 00 02 31  00 00 00 33" // P.VI SAW.  ...1  ...3
                    /*   10 */
                    + "d4 41 22 01  10 03 40 00    00 48 1f                ")
        // .A". ..@.  .H.

        val bytes: ByteArray? = BerTlvBuilder()
            .addHex(BerTag(0x50), "56495341")
            .addHex(BerTag(0x57), "1000023100000033D44122011003400000481F")
            .buildArray()
        assertArrayEquals(parseHex(hex), bytes)
    }

    @Test
    fun test91() {
        val b: BerTlvBuilder = template(TAG_E0)

        for (i: Int in 0..9) {
            b.addHex(TAG_86, "F9128478E28F860D8424000008514C8F")
        }
        val buf: ByteArray? = b.buildArray()
        println(HexUtil.toFormattedHexString(buf!!))
    }

    @Test
    fun testComplex() {
        val bytes: ByteArray = template(TAG_E0)
            .add(
                template(TAG_71)
                    .addHex(TAG_86, "F9128478E28F860D8424000008514C01")
                    .addHex(TAG_86, "F9128478E28F860D8424000008514C02")
            )
            .addHex(TAG_86, "F9128478E28F860D8424000008514C03")
            .buildArray()
        println(HexUtil.toFormattedHexString(bytes))

        val tlv = BerTlvParser().parseConstructed(bytes, 0, bytes.size)
        val list: List<BerTlv> = tlv.findAll(TAG_86)
        println("list = $list")
    }

    @Test
    fun testAddComplex() {
        val hex =  /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
            /*    0 */
            ("e0 81 91 71  7f 9f 18 04    12 34 56 78  86 0d 84 24" // ...q ....  .4Vx  ...$
                    /*   10 */
                    + "00 00 08 5a  e9 57 e8 81    8d 95 a8 86  0d 84 24 00" // ...Z .W..  ....  ..$.
                    /*   20 */
                    + "00 08 36 d2  44 95 47 47    ec 1e 86 0d  84 24 00 00" // ..6. D.GG  ....  .$..
                    /*   30 */
                    + "08 38 63 b1  c1 79 be 38    ac 86 0d 84  24 00 00 08" // .8c. .y.8  ....  $...
                    /*   40 */
                    + "25 4d b4 b4  ec db 21 74    86 0d 84 24  00 00 08 67" // %M.. ..!t  ...$  ...g
                    /*   50 */
                    + "8d f9 12 84  78 e2 8f 86    0d 84 24 00  00 08 51 4c" // .... x...  ..$.  ..QL
                    /*   60 */
                    + "8f d9 5a 21  6c 0b 86 0d    84 24 00 00  08 1f 62 34" // ..Z! l...  .$..  ..b4
                    /*   70 */
                    + "65 db 0c 95  59 86 0d 84    24 00 00 08  2a 5a 0a 9a" // e... Y...  $...  *Z..
                    /*   80 */
                    + "82 8a ba 0a  91 0a 42 50    0e 43 81 a4  67 7a 30 30" // .... ..BP  .C..  gz00
                    /*   90 */
                    + "8a 02 30 30                                         ")
        // ..00


        val expectedBuffer: ByteArray? = parseHex(hex)
        val source: BerTlv? = BerTlvParser().parseConstructed(expectedBuffer!!)

//        BerTlvLogger.log("....", source, BerTlvLoggerSlf4j())
        println(".... $source")
        run {
            val out: ByteArray? = BerTlvBuilder
                .from(source!!)
                .buildArray()
            val outTlv: BerTlv? = BerTlvBuilder
                .from(source)
                .buildTlv()

            println(".... $outTlv")
            assertArrayEquals(expectedBuffer, out)
        }

        run {
            val outTlv: BerTlv? = BerTlvBuilder().addBerTlv(source!!).buildTlv()
            val out: ByteArray? = BerTlvBuilder().addBerTlv(source).buildArray()

            println(".... $outTlv")
            assertArrayEquals(expectedBuffer, out)
        }
    }

    @Test
    fun addToTlvs() {
        //  [72]
        //  [9F18] 00009123
        //          [86] 84DA00CB0E0000000000008EBD6E174BAFE4E7
        //          [91] ED3C3B8B03928D0E0012
        val `in` = "721C9F180400009123861384DA00CB0E0000000000008EBD6E174BAFE4E7910AED3C3B8B03928D0E0012"

        // [72]
        // [9F18] 00009123
        //         [86] 84DA00CB0E0000000000008EBD6E174BAFE4E7
        //         [91] ED3C3B8B03928D0E0012
        // [8A] 00
        val out = "721C9F180400009123861384DA00CB0E0000000000008EBD6E174BAFE4E7910AED3C3B8B03928D0E00128A0100"

        val tlvs: BerTlvs? = BerTlvParser().parse(parseHex(`in`))


        val builder = BerTlvBuilder(tlvs!!)
        builder.addBerTlv(BerTlv(BerTag(0x8a), byteArrayOf(0x00)))

        assertEquals(out, HexUtil.toHexString(builder.buildArray()))
    }

    @Test
    fun constructedTagBytes(): Unit {
            val constructedTag = BerTlv(
                BerTag(0xE4),
                listOf(BerTlv(BerTag(0x86), parseHex("ED3C3B8B03928D0E0012")))
            )

            val builder = BerTlvBuilder()
            builder.addBerTlv(constructedTag)
            val bytes: ByteArray? = builder.buildArray()
            assertEquals(
                "[14] :  E4 0C 86 0A  ED 3C 3B 8B  03 92 8D 0E  00 12", HexUtil.toFormattedHexString(
                    bytes!!
                )
            )
        }

    @Test
    fun testBigData() {
        val bigData = ByteArray(1024)

        val builder = BerTlvBuilder()
        builder.addBytes(BerTag(0xd8), bigData)
        val berTlvs = builder.buildTlvs()
        println(".... $berTlvs")

        assertEquals(1, berTlvs.list.size)
    }

    @Test
    fun test_empty() {
        val tlv = BerTlv(BerTag(0x01), byteArrayOf())

        val bytes: ByteArray? = BerTlvBuilder(BerTag(0xe3))
            .addBerTlv(tlv)
            .buildArray()

        val berTlv: BerTlv? = BerTlvBuilder(BerTag(0xe3))
            .addBerTlv(tlv)
            .buildTlv()

        println(".... $berTlv")

        println("bytes = " + HexUtil.toFormattedHexString(bytes!!))
    }

    @Test
    fun test_empty_length() {
        val tlv = BerTlv(BerTag(0x01), byteArrayOf())

        val some = BerTlvBuilder()
            .addBytes(BerTag(0xE3), BerTlvBuilder().addBerTlv(tlv).buildArray())
            .buildTlv()

        assertEquals(some.tag, BerTag(0xe3))
        assertNotNull(some.values)
        assertEquals(1, some.values.size)
        val empty: BerTlv = some.values[0]
        assertEquals(BerTag(0x01), empty.tag)
        assertEquals("", empty.hexValue)
    }

    companion object {
        val TAG_E0: BerTag = BerTag(0xe0)
        val TAG_86: BerTag = BerTag(0x86)
        val TAG_71: BerTag = BerTag(0x71)
    }
}
