package com.sreeram.tlv

interface IBerTlvLogger {
    val isDebugEnabled: Boolean

    fun debug(aFormat: String?, vararg args: Any?)
}
