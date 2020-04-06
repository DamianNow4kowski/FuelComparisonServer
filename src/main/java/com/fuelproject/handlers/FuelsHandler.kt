package com.fuelproject.handlers

import com.google.gson.Gson
import java.io.IOException
import java.util.*


class FuelsHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val stationId = stationIdFromParam
        if (!stationId.isPresent) {
            writeFailResponse("Incorrect data")
            return
        }
        writeSuccessResponse(prepareResponse(stationId.get()))
    }

    private fun prepareResponse(stationId: Long): String {
        val parser = Gson()
        return parser.toJson(dbHelper!!.getFuels(stationId))
    }

    private val stationIdFromParam: Optional<Long>
        private get() {
            val stationId = params!!.bodyParams["stationId"]
            return try {
                Optional.of(stationId!!.toLong())
            } catch (e: NumberFormatException) {
                Optional.empty()
            }
        }
}
