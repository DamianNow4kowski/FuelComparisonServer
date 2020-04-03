package com.fuelproject.parsers

import com.sun.net.httpserver.HttpExchange
import java.io.IOException


class RequestParser(exchange: HttpExchange) {
    private val urlParams: MutableMap<String, String>
    private val bodyParams: MutableMap<String, String>
    private fun parse(exchange: HttpExchange) {
        if (exchange.requestURI != null) parse(exchange.requestURI.query, urlParams)
        if (exchange.requestBody != null) {
            try {
                parse(String(exchange.requestBody.readBytes()), bodyParams)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun parse(value: String?, container: MutableMap<String, String>) {
        if (value == null) return
        for (param in value.split("&").toTypedArray()) {
            val pair = param.split("=").toTypedArray()
            if (pair.size > 1) {
                container[pair[0]] = pair[1]
            } else {
                container[pair[0]] = ""
            }
        }
    }

    fun getBodyParams(): Map<String, String> {
        return bodyParams
    }

    fun getUrlParams(): Map<String, String> {
        return urlParams
    }

    init {
        urlParams = HashMap()
        bodyParams = HashMap()
        parse(exchange)
    }
}
