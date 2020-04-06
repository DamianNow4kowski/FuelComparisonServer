package com.fuelproject.handlers

import com.fuelproject.data.UserInfo
import com.mysql.cj.util.StringUtils.isNullOrEmpty
import org.json.JSONObject
import java.io.IOException
import java.util.*


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
        val loginResult: Optional<Long> = dbHelper!!.login(email, password)
        if (loginResult.isPresent) {
            if (dbHelper!!.isUserActive(loginResult.get())) {
                val data = prepareResponse(loginResult.get())
                writeSuccessResponse(data)
            } else {
                writeFailResponse("Account is blocked")
            }
        } else {
            writeFailResponse("Incorrect data")
        }
    }

    private fun prepareResponse(id: Long): JSONObject {
        val data = JSONObject()

        val userInfo: UserInfo = dbHelper!!.getUserInfo(id).get()
        data.put("name", userInfo.name)
        data.put("id", userInfo.id)
        data.put("email", userInfo.email)
        data.put("token", userInfo.token)
        return data
    }
}
