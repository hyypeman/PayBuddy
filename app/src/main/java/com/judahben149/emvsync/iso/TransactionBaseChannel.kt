package com.judahben149.emvsync.iso

import com.kelviniyalo.nexgoemvext.util.TcpSsLConnection
import org.jpos.iso.BaseChannel
import org.jpos.iso.ISOPackager
import java.net.SocketException
import java.util.concurrent.TimeoutException
import okhttp3.internal.and

class TransactionBaseChannel(host: String?, port: Int, packager: ISOPackager?) :
    BaseChannel(host, port, packager) {

    init {
        val sslSocketFactory = TcpSsLConnection().apply {
            setPassword("")
            setKeyStore("")
        }
        setSocketFactory(sslSocketFactory)
        setTimeOut(0)
    }

    private fun setTimeOut(timeout: Int) {
        try {
            if (timeout == 0) {
                super.setTimeout(60000)
            } else {
                super.setTimeout(timeout)
            }
        } catch (ex: SocketException) {
            try {
                throw TimeoutException("Transaction timed out!")
            } catch (exception: TimeoutException) {
                exception.printStackTrace()
            }
        }
    }

    override fun getHeaderLength(): Int {
        return 0
    }

    override fun sendMessageLength(len: Int) {
        super.sendMessageLength(len)
        serverOut.write((len shr 8) and 0xff)
        serverOut.write(len and 0xff)
    }

    override fun getMessageLength(): Int {
        var length = 0
        var array = ByteArray(2)

        while (length == 0) {
            serverIn.readFully(array, 0, 2)

            length = array[0] and 0xFF shl 8 or (array[1] and 0xFF)
            if (length == 0) {
                serverOut.write(array)
            }
            serverOut.flush()
        }
        return length
    }
}