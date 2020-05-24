package com.fuelproject.handlers

import org.json.JSONObject
import java.io.IOException

class UnlockUserHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val userId = params!!.bodyParams["userId"]
        unlockUser(userId)
    }

    @Throws(IOException::class)
    private fun unlockUser(userId: String?) {
        val unlockResult: Int = dbHelper!!.unlockUser(userId!!.toLong())
        if (unlockResult != -1) {
            val data = prepareResponse(unlockResult.toLong())
            writeSuccessResponse(data)
        } else {
            writeFailResponse("Niepoprawne dane")
        }
    }

    private fun prepareResponse(active: Long): JSONObject {
        val data = JSONObject()
        data.put("active", active)
        return data
    }
}