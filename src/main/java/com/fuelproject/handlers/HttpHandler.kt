package com.fuelproject.handlers

import com.fuelproject.database.DatabaseHelper
import com.fuelproject.parsers.RequestParser
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.IOException


abstract class HttpHandler : HttpHandler {
    var request: HttpExchange? = null
    var parser: RequestParser? = null
    var db: DatabaseHelper? = null

    @Throws(IOException::class)
    abstract fun handle()

    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        request = exchange
        parser = RequestParser(exchange)
//        db = db.getInstance()
//        db = db.
        handle()
    }

    @Throws(IOException::class)
    fun writeResponse(response: String) {
        request!!.sendResponseHeaders(200, response.length.toLong())
        val os = request!!.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}
