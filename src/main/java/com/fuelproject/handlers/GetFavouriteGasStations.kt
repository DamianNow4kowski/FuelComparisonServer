package com.fuelproject.handlers

import com.google.gson.Gson
import java.io.IOException
import java.util.*


class GetFavouriteGasStations : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val userId = getLongFromParam("userId")
        if (!userId.isPresent) {
            writeFailResponse("Incorrect data")
            return
        }
        val parser = Gson()
        val response = parser.toJson(dbHelper!!.getFavouriteGasStations(userId.get()))
        writeSuccessResponse(response)
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