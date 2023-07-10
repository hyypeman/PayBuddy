package com.judahben149.emvsync.domain

import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.iso.TransactionBaseChannel
import org.jpos.iso.ISOPackager

object SocketClient {

    fun getClient(ipAddress: String, port: String, packager: ISOPackager): TransactionBaseChannel {
        return TransactionBaseChannel(ipAddress, port.toInt(), packager)
    }
}