package com.fuelproject.handlers

import com.fuelproject.data.GasStation
import com.google.gson.Gson
import java.io.IOException


class RetrieveGasStationsHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val latNorth = params!!.bodyParams["latNorth"]!!.toDouble()
        val longEast = params!!.bodyParams["longEast"]!!.toDouble()
        val latSouth = params!!.bodyParams["latSouth"]!!.toDouble()
        val longWest = params!!.bodyParams["longWest"]!!.toDouble()

        val stations: List<GasStation>? = dbHelper!!.retrieveGasStations(latNorth, latSouth, longEast, longWest)
        writeSuccessResponse(prepareResponse(stations!!))
    }

    private fun prepareResponse(stations: List<GasStation>): String? {
        return Gson().toJson(stations)
    }
}
