package com.fuelproject.handlers

import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddFuelPriceHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val stationId = params!!.bodyParams["stationId"]!!.toLong()
        val fuelId = params!!.bodyParams["fuelId"]
        val price = params!!.bodyParams["price"]
        addFuelPrice(stationId, fuelId, price)
    }

    @Throws(IOException::class)
    private fun addFuelPrice(stationId: Long, fuelId: String?, price: String?) {
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.now()
        val priceAlreadyGivenToday: Boolean = dbHelper!!.priceAlreadyGivenToday(fuelId!!, price!!, dtf.format(localDate))
        val isSuccess: Boolean
        isSuccess = if (priceAlreadyGivenToday) {
            dbHelper!!.updateFuelPrice(fuelId!!, price!!, dtf.format(localDate))
        } else {
            dbHelper!!.addNewFuelPrice(fuelId!!, price, dtf.format(localDate))
        }
        if (isSuccess) {
            val responseContent = prepareResponse(stationId)
            writeSuccessResponse(responseContent)
        } else {
            writeFailResponse("Incorrect data")
        }
    }

    private fun prepareResponse(stationId: Long): JSONObject? {
        val data = JSONObject()
        data.put("stationId", stationId)
        return data
    }
}
