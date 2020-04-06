package com.fuelproject.handlers

import com.fuelproject.helpers.PasswordGenerator
import com.fuelproject.helpers.RemindPasswordEmail
import com.mysql.cj.util.StringUtils.isNullOrEmpty
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
        val isEmailInDB: Boolean = dbHelper!!.isEmailInDB(email)
        if (!isEmailInDB) {
            val passwordGenerator = PasswordGenerator()
            newPassword = passwordGenerator.generatePassword(8)

            val r = RemindPasswordEmail(newPassword!!)

            if (r.sendEmail(email)) {
                if (!dbHelper!!.updatePassword(email, newPassword!!)) {
                    val data: JSONObject = prepareResponse(email)
                    writeSuccessResponse(data)
                }
            } else {
                writeFailResponse("Incorrect data")
            }
        } else {
            writeFailResponse("Incorrect data")
        }
    }

    fun prepareResponse(email: String): JSONObject {
        val data = JSONObject()
        data.put("email", email)
        return data
    }
}
