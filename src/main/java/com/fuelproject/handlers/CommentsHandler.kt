package com.fuelproject.handlers


import com.google.gson.Gson
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*


class CommentsHandler : HttpHandler() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    override fun handle() {
        val stationId: Optional<Long> = getStationIdFromParam()
        if (!stationId.isPresent) {
            writeFailResponse("Incorrect data")
            return
        }
        writeSuccessResponse(prepareResponse(stationId.get()))
    }

    private fun prepareResponse(stationId: Long): String? {
        val parser = Gson()
        return parser.toJson(dbHelper!!.getComments(stationId))
    }

    private fun getStationIdFromParam(): Optional<Long> {
        val stationId: String = params!!.bodyParams["stationId"].toString()
        return try {
            return Optional.of(stationId.toLong())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            Optional.empty()
        }
    }
}
