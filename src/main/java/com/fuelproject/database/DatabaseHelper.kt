package com.fuelproject.database

import com.fuelproject.data.LoginResult
import com.fuelproject.data.UserInfo
import com.mysql.cj.jdbc.MysqlDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException


object DatabaseHelper {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var db: Connection? = null

    fun connect(address: String?, databaseName: String, userName: String?, password: String?): Boolean {
        return try {
            val dataSource = MysqlDataSource()
            dataSource.user = userName
            dataSource.password = password
            dataSource.serverName = address
            dataSource.characterEncoding = "UTF8"
            dataSource.serverTimezone = "UTC"

            db = dataSource.connection
            logger.info("Connection initialized with '${databaseName}' MySQL DB")

            db!!.createStatement().executeQuery("USE ${databaseName};")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun login(email: String?, password: String?): LoginResult? {
        //TODO: implement real one
        val loginResult = LoginResult()
        if (email == "correct" && password == "correct_pwd") {
            loginResult.status = true
            loginResult.id = 12345
        }
        return loginResult
    }

    fun generateToken(id: Long?): String? {
        //TODO: implement real one
        return "TOOOKEN"
    }

    fun getUserInfo(id: Long?): UserInfo {
        //TODO: implement real one
        val userInfo = UserInfo()
        userInfo.name = "User name"
        userInfo.id = 12345
        userInfo.email = "email@domain.com"
        return userInfo
    }
}
