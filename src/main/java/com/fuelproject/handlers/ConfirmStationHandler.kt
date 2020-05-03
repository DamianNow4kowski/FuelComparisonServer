package com.fuelproject.handlers

import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException


class ConfirmStationHandler : HttpHandler() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    override fun handle() {
        val stationId = params!!.bodyParams["stationId"]
        val userId = params!!.bodyParams["userId"]
        confirmStation(stationId, userId)
    }

    @Throws(IOException::class)
    private fun confirmStation(stationId: String?, userId: String?) {
        val isUserAllowed: Boolean = dbHelper!!.isUserAdmin(userId!!.toLong())
        if (isUserAllowed) {
            val confirmStatus: Int = dbHelper!!.confirmStation(stationId!!.toLong())
            if (confirmStatus != -1) {
                val data = prepareResponse(confirmStatus)
                writeSuccessResponse(data)
            } else {
                writeFailResponse("Incorrect data")
            }
        } else {
            writeFailResponse("User not allowed")
        }
    }

    private fun prepareResponse(confirmStatus: Int): JSONObject {
        val data = JSONObject()
        data.put("confirmStatus", confirmStatus)
        logger.info("Response: $data")
        return data
    }
}
