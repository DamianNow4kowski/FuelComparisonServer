package com.fuelproject.handlers

import com.fuelproject.data.UserInfo
import com.mysql.cj.util.StringUtils.isNullOrEmpty
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*


class LoginHandler : HttpHandler() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    override fun handle() {
        val email = params?.bodyParams?.get("email")
        val password = params?.bodyParams?.get("password")
        if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
            return writeFailResponse("Null or empty input")
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

        val stationAgentID: Long = dbHelper!!.isUserStationAgent(userInfo.id.toString())
        data.put("stationAgentID", stationAgentID)
        return data
    }
}
