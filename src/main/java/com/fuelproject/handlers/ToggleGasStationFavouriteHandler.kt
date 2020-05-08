package com.fuelproject.handlers

import java.io.IOException
import java.util.*


class ToggleGasStationFavouriteHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val stationId = getLongFromParam("stationId")
        val userId = getLongFromParam("userId")
        if (!stationId.isPresent || !userId.isPresent) {
            writeFailResponse("Incorrect data")
            return
        }
        if (dbHelper!!.isGasStationFavourite(userId.get(), stationId.get())) {
            dbHelper!!.deleteGasStationFavourite(userId.get(), stationId.get())
        } else {
            dbHelper!!.insertGasStationFavourite(userId.get(), stationId.get())
        }
        writeSuccessResponse("Added fav station")
    }

    private fun getLongFromParam(paramName: String): Optional<Long> {
        val value = params!!.bodyParams[paramName]
        return try {
            Optional.of(value!!.toLong())
        } catch (e: NumberFormatException) {
            Optional.empty()
        }
    }
}