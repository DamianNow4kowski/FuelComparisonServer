package com.fuelproject.handlers

import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddFuelPriceHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val fuelId = params!!.bodyParams["fuelId"]
        val price = params!!.bodyParams["price"]
        addFuelPrice(fuelId, price)
    }

    @Throws(IOException::class)
    private fun addFuelPrice(fuelId: String?, price: String?) {
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
            val responseContent = java.lang.Boolean.toString(true)
            writeSuccessResponse(responseContent)
        } else {
            writeFailResponse("Incorrect data")
        }
    }
}
