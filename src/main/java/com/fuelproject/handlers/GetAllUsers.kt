package com.fuelproject.handlers

import com.google.gson.Gson
import java.io.IOException

class GetAllUsers : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val parser = Gson()
        val response = parser.toJson(dbHelper!!.getAllUsers())
        writeSuccessResponse(response)
    }
}
