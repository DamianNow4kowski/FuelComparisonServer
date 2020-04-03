package com.fuelproject.handlers

import com.fuelproject.database.DatabaseHelper
import com.fuelproject.http.RequestParams
import com.fuelproject.parsers.RequestParser
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.HttpURLConnection


abstract class HttpHandler : HttpHandler {
    private val logger = LoggerFactory.getLogger(javaClass)
    var request: HttpExchange? = null
    var params: RequestParams? = null
    var dbHelper: DatabaseHelper? = null

    @Throws(IOException::class)
    abstract fun handle()

    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        logger.info("Handling HTTP ${exchange.requestMethod} request from URL: ${exchange.requestURI}")
        request = exchange
        params = RequestParser.parse(exchange)
        logger.info("RequestParams URLParams: ${params!!.urlParams}")
        logger.info("RequestParams bodyParams: ${params!!.bodyParams}")
        handle()
    }

    @Throws(IOException::class)
    fun writeResponse(response: String) {
        request!!.sendResponseHeaders(200, response.length.toLong())
        val os = request!!.responseBody
        os.write(response.toByteArray())
        os.close()
    }

    @Throws(IOException::class)
    open fun writeFailResponse(reason: String) {
        val result = JSONObject()
        result.put("status", HttpURLConnection.HTTP_INTERNAL_ERROR)
        result.put("errorCode", HttpURLConnection.HTTP_BAD_REQUEST)
        result.put("reason", reason)
        writeResponse(result.toString())
    }

    @Throws(IOException::class)
    open fun writeSuccessResponse(data: Any?) {
        val result = JSONObject()
        result.put("status", HttpURLConnection.HTTP_OK)
        result.put("data", data)
        writeResponse(result.toString())
    }

    fun databaseHelper(databaseHelper: DatabaseHelper) {
        dbHelper = databaseHelper
    }
}
