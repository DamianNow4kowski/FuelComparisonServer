package com.fuelproject.database

import com.fuelproject.data.Comment
import com.fuelproject.data.GasStation
import com.fuelproject.data.UserInfo
import com.mysql.cj.jdbc.MysqlDataSource
import org.apache.ibatis.jdbc.ScriptRunner
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.Reader
import java.sql.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Optional.empty
import java.util.UUID


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

//    end database section

    //    /login
    fun isUserActive(userID: Long): Boolean {
        val query = "SELECT active FROM user WHERE user_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userID)
            val result = stm.executeQuery()
            if (result.first()) {
                val isActive = result.getBoolean("active")
                result.close()
                isActive
            } else {
                result.close()
                false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
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

    //    /register
    fun addNewUser(email: String?, username: String?, password: String?, isAgent: String?): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
        val addUserQuery = "INSERT INTO user (username, password, email, token, joined, station_agent, superuser, active) VALUES (?, ?, ?, ?, ?, ?, 0, ?);"
        val isAgentBool = java.lang.Boolean.parseBoolean(isAgent)
        val isActive = !isAgentBool
        return try {
            val stm: CallableStatement = db!!.prepareCall(addUserQuery)
            stm.setString(1, username)
            stm.setString(2, password)
            stm.setString(3, email)
            stm.setString(4, UUID.randomUUID().toString().replace("-", ""))
            stm.setString(5, currentDate)
            stm.setBoolean(6, isAgentBool)
            stm.setBoolean(7, isActive)
            stm.executeUpdate()
            stm.close()
            logger.info("Added new user to db")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun checkUniqueUsernameAndEmail(username: String?, email: String?): Boolean {
        val query = "SELECT * FROM user WHERE username = ? OR email = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, username)
            stm.setString(2, email)
            val result = stm.executeQuery()
            !result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    //    remindPassword
    fun checkUniqueEmail(email: String?): Boolean {
        val query = "SELECT * FROM user WHERE email = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, email)
            val result = stm.executeQuery()
            !result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun updatePassword(email: String, password: String): Boolean {
        val query = "UPDATE user SET password = '$password' WHERE email='$email'"
        return try {
            val stm = db!!.createStatement()
            stm.execute(query)
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    //    gaz station
    fun addNewGasStation(gasStation: GasStation): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
        val insertQuery = "INSERT INTO gas_station(agent_id, name, address, added_on, electric_charging," +
                "accepted, for_disabled_people, lat, lng) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"

        try {
            var newStationId: Int = -1
            db!!.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS).use { statement ->
                statement.setInt(1, 0)
                statement.setString(2, gasStation.name)
                statement.setString(3, gasStation.address)
                statement.setString(4, currentDate)
                statement.setBoolean(5, gasStation.isForElectricCars)
                statement.setBoolean(6, false)
                statement.setBoolean(7, gasStation.isForDisabledPeople)
                statement.setDouble(8, gasStation.latitude)
                statement.setDouble(9, gasStation.longitude)

                val rs = statement.generatedKeys
                if (rs.next()) {
                    newStationId = rs.getInt(1)
                }
                return newStationId > 0 &&
                        addGasStationOpeningHours(newStationId, gasStation.openFrom, gasStation.openTo) &&
                        addGasStationAvailablePetrol(newStationId, gasStation)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    //    comment
    open fun getComments(gasStationId: Long): MutableList<Comment> {
        val query = "SELECT * FROM comment WHERE station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, gasStationId)
            val results = stm.executeQuery()
            val comments: MutableList<Comment> = LinkedList<Comment>()
            while (results.next()) {
                val comment = Comment()
                comment.id = results.getLong("comment_id")
                comment.user_id = results.getLong("user_id")
                comment.station_id = results.getLong("station_id")
                comment.rating = results.getLong("rating")
                comment.content = results.getString("content")
                comments.add(comment)
            }
            results.close()
            comments
        } catch (e: SQLException) {
            e.printStackTrace()
            return LinkedList<Comment>()
        }
    }

    fun retrieveGasStations(latNorth: Double, latSouth: Double,
                            longEast: Double, longWest: Double): List<GasStation>? {
        val stations: MutableList<GasStation> = LinkedList()
        val query = "SELECT * FROM gas_station WHERE lat BETWEEN ? AND ? AND lng BETWEEN ? AND ?"
        try {
            db!!.prepareCall(query).use { statement ->
                statement.setDouble(1, latSouth)
                statement.setDouble(2, latNorth)
                statement.setDouble(3, longWest)
                statement.setDouble(4, longEast)
                val result = statement.executeQuery()
                while (result.next()) {
                    val gasStation = GasStation()
                    gasStation.id = result.getInt("station_id").toLong()
                    gasStation.name = result.getString("name")
                    gasStation.address = result.getString("address")
                    gasStation.isForElectricCars = result.getBoolean("electric_charging")
                    gasStation.isForDisabledPeople = result.getBoolean("for_disabled_people")
                    gasStation.latitude = result.getDouble("lat")
                    gasStation.longitude = result.getDouble("lng")
                    fillGasStationsOpeningHours(gasStation)
                    stations.add(gasStation)
                }
                result.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return stations
    }

    private fun addGasStationOpeningHours(gasStationId: Int, openFrom: String, openTo: String): Boolean {
        val insertQuery = "INSERT INTO opening_hours(station_id, start_time, end_time) VALUES(?, ?, ?)"
        try {
            db!!.prepareStatement(insertQuery).use { statement ->
                statement.setInt(1, gasStationId)
                statement.setString(2, openFrom)
                statement.setString(3, openTo)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun addGasStationAvailablePetrol(gasStationId: Int, gasStation: GasStation): Boolean {
        return true // TODO
    }

    private fun fillGasStationsOpeningHours(gasStation: GasStation) {
        val query = "SELECT * FROM opening_hours WHERE station_id = ?"
        try {
            db!!.prepareCall(query).use { statement ->
                statement.setLong(1, gasStation.id)
                val result = statement.executeQuery()
                if (result.next()) {
                    gasStation.openFrom = result.getString("start_time")
                    gasStation.openTo = result.getString("end_time")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}
