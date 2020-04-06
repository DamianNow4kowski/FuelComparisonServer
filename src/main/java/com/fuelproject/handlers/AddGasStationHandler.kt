package com.fuelproject.handlers

import com.fuelproject.data.GasStation
import com.google.gson.Gson
import java.io.IOException


class AddGasStationHandler : HttpHandler() {
    override fun handle() {
        val stationJson = params!!.bodyParams["stationData"]
        val gasStation = Gson().fromJson(stationJson, GasStation::class.java)
        addGasStation(gasStation)
    }

    @Throws(IOException::class)
    private fun addGasStation(gasStation: GasStation) {
        val isSuccess: Boolean = dbHelper!!.addNewGasStation(gasStation)
        if (isSuccess) {
            val responseContent = prepareSuccessResponse(gasStation)
            writeSuccessResponse(responseContent)
        } else {
            writeFailResponse("Error during insert gas station")
        }
    }

    private fun prepareSuccessResponse(gasStation: GasStation): String {
        return Gson().toJson(gasStation)
    }
}
