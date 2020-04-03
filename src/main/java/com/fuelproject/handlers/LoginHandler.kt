package com.fuelproject.handlers

import com.fuelproject.data.LoginResult
import com.fuelproject.data.UserInfo
import org.json.JSONObject
import java.io.IOException


class LoginHandler : HttpHandler() {
    @Throws(IOException::class)
    override fun handle() {
        val email = params!!.bodyParams["email"]
        val password = params!!.bodyParams["password"]
        if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
            writeFailResponse("Null or empty input")
            return
        }
        tryLogIn(email, password)
    }

    @Throws(IOException::class)
    private fun tryLogIn(email: String?, password: String?) {
        val loginResult: LoginResult? = dbHelper!!.login(email, password)
        if (loginResult!!.status) {
            val data = prepareResponse(loginResult.id)
            writeSuccessResponse(data)
        } else {
            writeFailResponse("Incorrect data")
        }
    }

    private fun isNullOrEmpty(value: String?): Boolean {
        return value == null || value.isEmpty()
    }

    private fun prepareResponse(id: Long): JSONObject {
        val data = JSONObject()
        data.put("token", dbHelper!!.generateToken(id))
        val userInfo: UserInfo = dbHelper!!.getUserInfo(id)
        data.put("name", userInfo.name)
        data.put("id", userInfo.id)
        data.put("email", userInfo.email)
        return data
    }
}