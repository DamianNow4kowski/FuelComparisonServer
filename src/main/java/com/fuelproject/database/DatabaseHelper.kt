package com.fuelproject.database

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
}
