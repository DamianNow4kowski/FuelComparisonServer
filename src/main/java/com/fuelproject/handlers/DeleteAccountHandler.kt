package com.fuelproject.handlers

import com.mysql.cj.util.StringUtils
import java.io.IOException


class DeleteAccountHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val email = params!!.bodyParams["email"]
        if (StringUtils.isNullOrEmpty(email)) {
            writeFailResponse("Incorrect data")
            return
        }
        removeAccount(email)
    }

    @Throws(IOException::class)
    private fun removeAccount(email: String?) {
        val isEmailInDB: Boolean = dbHelper!!.isEmailInDB(email)
        if (isEmailInDB) {
            if (dbHelper!!.deleteAccount(email!!)) {
                val responseContent = java.lang.Boolean.toString(true)
                writeSuccessResponse(responseContent)
            } else writeFailResponse("Error during deleting account")
        } else writeFailResponse("Incorrect data")
    }
}
