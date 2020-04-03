package com.fuelproject.database

import com.fuelproject.data.UserInfo
import com.mysql.cj.jdbc.MysqlDataSource
import org.apache.ibatis.jdbc.ScriptRunner
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.Reader
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import java.util.Optional.empty


object DatabaseHelper {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var db: Connection? = null

    fun connect(address: String, userName: String, password: String): Boolean {

        return try {
            val dataSource = MysqlDataSource()
            dataSource.user = userName
            dataSource.password = password
            dataSource.serverName = address
            dataSource.characterEncoding = "UTF8"
            dataSource.serverTimezone = "UTC"

            db = dataSource.connection
            logger.info("Connection initialized")

            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun selectDatabase(databaseName: String): Boolean {
        return try {
            db!!.createStatement().executeQuery("USE `${databaseName}`;")
            logger.info("Selected DB: '${databaseName}'")
            true
        } catch (e: SQLException) {
//            e.printStackTrace()
            false
        }
    }

    fun dropDatabase(databaseName: String) {
        val query = "DROP DATABASE IF EXISTS `$databaseName`;"
        try {
            val stm: Statement = db!!.createStatement()
            stm.execute(query)
            logger.info("Dropped '$databaseName'")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun createDatabase(databaseName: String) {
        val query = "CREATE DATABASE `$databaseName`; "
        logger.info("Creating database '$databaseName'...")
        try {
            val stm = db!!.createStatement()
            stm.execute(query)
            stm.close()
            logger.info("Created database '$databaseName'")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fillDatabase(fileName: String?, databaseName: String) {
        try {
            db!!.createStatement().executeQuery("USE `${databaseName}`;")
            val sr = ScriptRunner(db)
            val reader: Reader = BufferedReader(FileReader(fileName))
            sr.runScript(reader)

            logger.info("Filled db with queries...")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun login(email: String?, password: String?): Optional<Long> {
        val query = "SELECT user_id FROM user WHERE email = ? AND password = ?"

        try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, email)
            stm.setString(2, password)
            val result: ResultSet = stm.executeQuery()
            if (result.first()) {
                var id: Long = result.getLong("user_id")
                result.close()
                return Optional.of(id)
            } else {
                result.close()
                return empty()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return empty()
        }
    }

    fun generateToken(id: Long?): String? {
        //TODO: implement real one
        return "TOOOKEN"
    }

    fun getUserInfo(id: Long?): Optional<UserInfo> {
        val query = "SELECT * FROM user WHERE user_id = ?"

        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, id!!)
            val result = stm.executeQuery()
            if (result.first()) {
                val info = UserInfo()
                info.email = result.getString("email")
                info.id = result.getLong("user_id")
                info.name = result.getString("username")
                info.token = result.getString("token")
                result.close()
                Optional.of(info)
            } else {
                empty()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            empty()
        }
    }
}
