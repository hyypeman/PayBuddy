package com.judahben149.emvsync.domain

import com.judahben149.emvsync.iso.TransactionBaseChannel
import com.judahben149.emvsync.utils.logThis
import org.jpos.iso.ISOPackager
import java.net.URI

object SocketClient {

    /**
     * Default URL is always "https://EFTDEBITATM.FNFIS.COM" if we need to change to CDS
     * Refactor the code to pass it in as a param
     */
    fun getClient(url: String, packager: ISOPackager): TransactionBaseChannel {
        val uri = URI(url)
        val host = uri.host
        val port = if (uri.port == -1) {
            // Default ports for HTTP/HTTPS if not specified
            if (uri.scheme == "https") 443 else 80
        } else {
            uri.port
        }
        "Creating client to connect to $host:$port".logThis("SocketClient")
        return TransactionBaseChannel(host, port, packager)
    }
}
