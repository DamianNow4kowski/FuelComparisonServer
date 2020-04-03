package com.fuelproject.handlers

import com.fuelproject.data.UserInfo
import com.fuelproject.helpers.PasswordGenerator
import com.fuelproject.helpers.RemindPasswordEmail
import org.json.JSONObject
import java.io.IOException


class RemindPasswordHandler : HttpHandler() {
    private var newPassword: String? = null

    @Throws(IOException::class)
    override fun handle() {
        val email = params!!.bodyParams["email"]
        if (isNullOrEmpty(email)) {
            writeFailResponse("Null or empty input")
            return
        }

        changePassword(email!!)
    }

    @Throws(IOException::class)
    private fun changePassword(email: String) {
        val isEmailUnique: Boolean = dbHelper!!.checkUniqueEmail(email)
        if (!isEmailUnique) {
            val passwordGenerator = PasswordGenerator()
            newPassword = passwordGenerator.generatePassword(8)
            if (!dbHelper!!.updatePassword(email, newPassword!!)) {
                val r = RemindPasswordEmail(newPassword!!)
                r.sendEmail(email)
                val data = prepareResponse(2)
                writeSuccessResponse(data)
            } else {
                writeFailResponse("Incorrect data")
            }
        } else {
            writeFailResponse("Incorrect data")
        }
    }

    private fun prepareResponse(id: Long): JSONObject {
        val data = JSONObject()
        val userInfo: UserInfo = dbHelper!!.getUserInfo(id).get()
        data.put("name", userInfo.name)
        return data
    }

    private fun isNullOrEmpty(value: String?): Boolean {
        return value == null || value.isEmpty()
    }
}
