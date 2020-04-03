package com.fuelproject.factories

import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress


object HttpServerFactory {
    @Throws(IOException::class)
    fun create(port: Int): HttpServer {
        return HttpServer.create(InetSocketAddress(port), 0)
    }
}
