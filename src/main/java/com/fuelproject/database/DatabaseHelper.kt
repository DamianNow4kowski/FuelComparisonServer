package com.fuelproject.database

import com.fuelproject.data.LoginResult
import com.fuelproject.data.UserInfo
import com.mysql.cj.jdbc.MysqlDataSource
import org.apache.ibatis.jdbc.ScriptRunner
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.util.*


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
            e.printStackTrace()
            false
        }
    }

    fun restartDatabase(databaseName: String, fileName: String?) {
        val query = "DROP DATABASE IF EXISTS `$databaseName`;"
        try {
            val stm: Statement = db!!.createStatement()
            stm.execute(query)
            logger.info("Dropped $databaseName")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        createDatabase(databaseName, fileName)
    }

    fun createDatabase(databaseName: String, fileName: String?) {
        val query = "CREATE DATABASE `$databaseName`; "
        try {
            val stm = db!!.createStatement()
            stm.execute(query)
            stm.close()
            logger.info("Created database $databaseName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fillDatabase(fileName, databaseName)
    }

    private fun fillDatabase(fileName: String?, databaseName: String) {
        val query_create = "CREATE DATABASE ${databaseName}; "
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

    @Throws(IOException::class)
    private fun readFile(fileName: String): LinkedList<String> {
        val queries = LinkedList<String>()
        var content = ""
        val reader = BufferedReader(FileReader(fileName))
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {
            if (!line!!.startsWith("--") && !line!!.startsWith("/*")) {
                content += line
                if (line!!.endsWith(";")) {
                    queries.add(content)
                    content = ""
                }
            }
        }
        return queries
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
