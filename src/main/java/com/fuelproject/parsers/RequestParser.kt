package com.fuelproject.parsers

import com.fuelproject.http.RequestParams
import com.sun.net.httpserver.HttpExchange
import org.slf4j.LoggerFactory
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder


object RequestParser {
    private lateinit var exchange: HttpExchange
    private val logger = LoggerFactory.getLogger(javaClass)


    fun parse(exchange: HttpExchange): RequestParams {
        this.exchange = exchange
        val params = RequestParams()
        params.urlParams = parseURI()
        params.bodyParams = parseBody()
        return params
    }

    private fun parseBody(): MutableMap<String, String> {
        return parse(String(exchange.requestBody.readBytes()))
    }

    private fun parseURI(): MutableMap<String, String> {
        val uri: URI? = exchange.requestURI
        return parse(uri?.rawQuery)
    }

    private fun parse(value: String?): MutableMap<String, String> {
        val container: MutableMap<String, String> = HashMap()
        if (value != null) {
            for (param in value.split("&").toTypedArray()) {
                val pair = param.split("=")
                if (pair.size > 1) {
                    container[pair[0]] = decode(pair[1])
                } else {
                    container[pair[0]] = ""
                }
            }
            return container
        } else
            return container
    }

    private fun decode(value: String): String {
        return try {
            URLDecoder.decode(value, "UTF-8").toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            value
        }
    }
}
