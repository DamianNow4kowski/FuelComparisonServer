package com.fuelproject.handlers

import org.json.JSONObject
import java.io.IOException


class RegisterHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val email = params!!.bodyParams["email"]
        val username = params!!.bodyParams["username"]
        val password = params!!.bodyParams["password"]
        val isAgent = params!!.bodyParams["isAgent"]
        if (isUserDataInvalid(email, username, password, isAgent)) {
            writeFailResponse("Null or empty input")
            return
        }
        addNewUser(email, username, password, isAgent)
    }

    @Throws(IOException::class)
    private fun addNewUser(email: String?, username: String?, password: String?, isAgent: String?) {
        val isUsernameAndEmailUnique: Boolean = dbHelper!!.checkUniqueUsernameAndEmail(username, email)
        if (isUsernameAndEmailUnique) {
            val registerResult: Boolean = dbHelper!!.addNewUser(email, username, password, isAgent)
            if (registerResult) {
                val data = prepareResponse(java.lang.Boolean.parseBoolean(isAgent))
                writeSuccessResponse(data)
            } else {
                writeFailResponse("Incorrect data")
            }
        } else {
            writeFailResponse("Username or email taken")
        }
    }

    private fun isNullOrEmpty(value: String?): Boolean {
        return value == null || value.isEmpty()
    }

    private fun prepareResponse(isAgent: Boolean): JSONObject {
        val data = JSONObject()
        data.put("isAgent", isAgent)
        return data
    }

    private fun isUserDataInvalid(email: String?, username: String?, password: String?, isAgent: String?): Boolean {
        return isNullOrEmpty(email) || isNullOrEmpty(username) || isNullOrEmpty(password) || isNullOrEmpty(isAgent)
    }
}
