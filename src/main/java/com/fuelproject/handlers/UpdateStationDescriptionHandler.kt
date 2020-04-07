package com.fuelproject.handlers

import java.io.IOException


class UpdateStationDescriptionHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val userID = params!!.bodyParams["user_id"]
        val stationID = params!!.bodyParams["station_id"]
        val description = params!!.bodyParams["description"]
        updateDescription(userID, stationID, description)
    }

    @Throws(IOException::class)
    private fun updateDescription(userID: String?, stationID: String?, description: String?) {
        val isUserStationAgent: Boolean = dbHelper!!.checkStationAgent(userID!!, stationID!!)
        if (isUserStationAgent) {
            val updateDescriptionResult: Boolean = dbHelper!!.updateStationDescription(stationID, description)
            if (updateDescriptionResult) {
                val responseContent = java.lang.Boolean.toString(true)
                writeSuccessResponse(responseContent)
            } else {
                writeFailResponse("Updating description failed")
            }
        } else {
            writeFailResponse("Not station agent")
        }
    }
}
