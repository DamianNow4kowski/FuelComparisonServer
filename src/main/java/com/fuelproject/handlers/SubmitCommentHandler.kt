package com.fuelproject.handlers

import java.io.IOException


class SubmitCommentHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val userID = params!!.bodyParams["user_id"]
        val stationID = params!!.bodyParams["station_id"]
        val commentRate = params!!.bodyParams["rating"]
        val commentBody = params!!.bodyParams["content"]
        addNewComment(userID, stationID, commentBody, commentRate)
    }

    @Throws(IOException::class)
    private fun addNewComment(userID: String?, stationID: String?, commentBody: String?, commentRate: String?) {
        val addCommentResult: Boolean = dbHelper!!.addNewComment(userID!!, stationID!!, commentBody, commentRate!!)
        if (addCommentResult) {
            val responseContent = java.lang.Boolean.toString(true)
            writeSuccessResponse(responseContent)
        } else {
            writeFailResponse("Incorrect data")
        }
    }
}
