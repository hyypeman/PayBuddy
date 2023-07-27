package com.judahben149.emvsync.iso

import com.judahben149.emvsync.utils.logThis
import okhttp3.internal.and
import org.jpos.iso.BaseChannel
import org.jpos.iso.ISOPackager
import org.jpos.iso.channel.NACChannel
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.TimeoutException

class TransactionBaseChannel(host: String?, port: Int, packager: ISOPackager?) :
    BaseChannel(host, port, packager) {

    init {
        val sslSocketFactory = TCPSSLCONNECTION()
        sslSocketFactory.setPassword("")
        sslSocketFactory.setKeyStore("")
        setSocketFactory(sslSocketFactory)
        setTimeOut(0)
    }

    private fun setTimeOut(timeout: Int) {
        try {
            if (timeout == 0) {
                "Timeout is 60secs".logThis("TT")
                super.setTimeout(60000)
            } else {
                "Timeout is 0secs".logThis("TT")
                super.setTimeout(timeout)
            }
        } catch (ex: SocketException) {
            try {
                throw TimeoutException("Transaction timed out!")
                "Threw timeout exception".logThis("TT")
            } catch (exception: TimeoutException) {
                "Couldn't throw timeout exception - error(${exception.message})".logThis("TT")
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
        "Send message length - $len".logThis("TT")
    }

    override fun getMessageLength(): Int {
        var length = 0
        val array = ByteArray(2)

        while (length == 0) {
//            serverIn.readFully(array, 0, 2)

            readFully(serverIn, array, 0, 2)

            length = array[0] and 0xFF shl 8 or (array[1] and 0xFF)
            if (length == 0) {
                serverOut.write(array)
            }
            serverOut.flush()
        }
        "Get message length - $length".logThis("TT")
        return length
    }


    private fun readFully(`in`: DataInputStream, b: ByteArray?, off: Int, len: Int): Int {
        var count: Int
        var n = 0
        while (n < len) {
            try {
                println("readFully of " + (len - n) + " bytes at offset " + (off + n))
                count = `in`.read(b, off + n, len - n)
                println("readFully count=$count")
            } catch (e: IOException) {
                /* Try again */
                println("readFully: exception $e")
                continue
            }
            if (count <= 0) throw EOFException()
            n += count
        }
        return n
    }
}