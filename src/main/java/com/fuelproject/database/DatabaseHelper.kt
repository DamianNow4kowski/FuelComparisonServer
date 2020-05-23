package com.fuelproject.database

import com.fuelproject.data.Comment
import com.fuelproject.data.Fuel
import com.fuelproject.data.GasStation
import com.fuelproject.data.UserInfo
import com.mysql.cj.jdbc.MysqlDataSource
import org.apache.ibatis.jdbc.ScriptRunner
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.Reader
import java.sql.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    fun deleteAccount(email: String): Boolean {
        val query = " DELETE from user WHERE email='$email'"
        return try {
            val stm = db!!.createStatement()
            stm.execute(query)
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
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

    fun isUsernameOrEmailInDB(username: String?, email: String?): Boolean {
        val query = "SELECT * FROM user WHERE username = ? OR email = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, username)
            stm.setString(2, email)
            val result = stm.executeQuery()
            result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    //    remindPassword
    fun isEmailInDB(email: String?): Boolean {
        val query = "SELECT * FROM user WHERE email = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, email)
            val result = stm.executeQuery()
            result.first()
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
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    //    gas station
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
                statement.executeUpdate()

                val rs = statement.generatedKeys
                if (rs.next()) {
                    newStationId = rs.getInt(1)
                }
                addGasStationOpeningHours(newStationId, gasStation.openFrom, gasStation.openTo)
                addGasStationAvailablePetrol(newStationId, gasStation)
                return newStationId > 0

            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun confirmStation(stationId: Long): Int {
        val query = "UPDATE gas_station SET accepted = NOT accepted WHERE station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, stationId)
            stm.executeUpdate()
            if (isStationAccepted(stationId)) 1 else 0
        } catch (e: SQLException) {
            e.printStackTrace()
            -1
        }
    }

    private fun isStationAccepted(stationId: Long): Boolean {
        val query = "SELECT accepted FROM gas_station WHERE station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, stationId)
            val result = stm.executeQuery()
            if (result.first()) {
                val isAccepted = result.getBoolean("accepted")
                result.close()
                isAccepted
            } else {
                result.close()
                false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun isUserAdmin(userId: Long): Boolean {
        val query = "SELECT superuser FROM user WHERE user_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userId)
            val result = stm.executeQuery()
            if (result.first()) {
                val isAdmin = result.getBoolean("superuser")
                result.close()
                isAdmin
            } else {
                result.close()
                false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
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

    fun addNewComment(userID: String, stationID: String, body: String?, rate: String): Boolean {
        val query = "INSERT INTO `comment` (`user_id`, `station_id`, `rating`, `content`) VALUES (?, ?, ?, ?)"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userID.toLong())
            stm.setLong(2, stationID.toLong())
            stm.setInt(3, rate.toInt())
            stm.setString(4, body)
            val result = stm.executeUpdate()
            result > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun userAlreadyCommented(userID: String, stationID: String): Boolean {
        val query = "SELECT * FROM comment WHERE user_id = ? and station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userID.toLong())
            stm.setLong(2, stationID.toLong())
            val result = stm.executeQuery()
            result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun updateUserComment(userID: String, stationID: String, body: String?, rate: String): Boolean {
        val query = "UPDATE comment comm set comm.content = ?, comm.rating = ? WHERE user_id=? and station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, body)
            stm.setInt(2, rate.toInt())
            stm.setLong(3, userID.toLong())
            stm.setLong(4, stationID.toLong())
            val result = stm.executeUpdate()
            result > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun checkStationAgent(userID: String, stationID: String): Boolean {
        val query = "SELECT u.active FROM user u join gas_station gs on u.user_id=gs.agent_id where u.user_id=? and gs.station_id=?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userID.toLong())
            stm.setLong(2, stationID.toLong())
            val result = stm.executeQuery()
            result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun isUserStationAgent(userID: String): Long {
        val query = "Select gs.station_id from gas_station gs where gs.agent_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userID.toLong())
            val result = stm.executeQuery()
            if (result.first()) {
                result.getLong("station_id")
            } else -1
        } catch (e: SQLException) {
            e.printStackTrace()
            -1
        }
    }

    fun updateStationDescription(stationID: String, content: String?): Boolean {
        val query = "Update gas_station gs set gs.description = ? where gs.station_id = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setString(1, content)
            stm.setLong(2, stationID.toLong())
            val result = stm.executeUpdate()
            result > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
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
        val insertQuery = "INSERT INTO available_fuel(station_id, fuel_kind_id) VALUES(?, ?)"
        for (i in 1..4) {
            try {
                val statement: PreparedStatement = db!!.prepareStatement(insertQuery)
                statement.setInt(1, gasStationId)
                statement.setString(2, i.toString())

                logger.info(statement.toString())

                statement.executeUpdate()
            } catch (e: SQLException) {
                e.printStackTrace()
                return false
            }
        }
        return true
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


    //    fuel
    fun getFuels(gasStationId: Long): MutableList<Fuel> {
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.now()

        val query = "SELECT af.available_fuel_id, fk.name, fp.price " +
                "FROM available_fuel af " +
                "JOIN fuel_kind fk ON af.fuel_kind_id = fk.fuel_kind_id " +
                "LEFT JOIN fuel_price fp ON af.available_fuel_id = fp.available_fuel_id AND " +
                "fp.fuel_price_id=( SELECT max(fuel_price_id) FROM fuel_price) " +
                "WHERE af.station_id = ? " +
                "GROUP BY af.available_fuel_id"

//        val query = "SELECT af.available_fuel_id, fk.name, fp.price " +
//                "FROM available_fuel af " +
//                "JOIN fuel_kind fk ON af.fuel_kind_id = fk.fuel_kind_id " +
//                "LEFT JOIN fuel_price fp ON af.available_fuel_id = fp.available_fuel_id AND fp.added_on = ? " +
//                "WHERE af.station_id = ? " +
//                "AND (fp.rating IS NULL " +
//                "OR fp.rating = (SELECT max(rating) FROM fuel_price fp2 where available_fuel_id = af.available_fuel_id))" +
//                "GROUP BY af.available_fuel_id"

        return try {
            val stm = db!!.prepareCall(query)
//            stm.setDate(1, Date.valueOf(dtf.format(localDate)))
//            logger.info(Date.valueOf(dtf.format(localDate)).toString())
            stm.setLong(1, gasStationId)
            val results = stm.executeQuery()
            val fuels: MutableList<Fuel> = LinkedList()
            while (results.next()) {
                val fuel = Fuel()
                fuel.fuelId = results.getLong("available_fuel_id")
                fuel.price = results.getFloat("price")
                fuel.name = results.getString("name")
                fuels.add(fuel)
                logger.info(results.toString())
            }
            results.close()
            logger.info(results.toString())
            logger.info(fuels.toString())
            fuels
        } catch (e: SQLException) {
            e.printStackTrace()
            LinkedList()
        }
    }

    fun priceAlreadyGivenToday(fuelId: String, price: String, date: String?): Boolean {
        val query = "SELECT * FROM available_fuel af " +
                "LEFT JOIN fuel_price fp ON af.available_fuel_id = fp.available_fuel_id " +
                "WHERE af.available_fuel_id = ? AND fp.price = ? AND added_on = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, fuelId.toLong())
            stm.setDouble(2, price.toDouble())
            stm.setDate(3, Date.valueOf(date))
            val result = stm.executeQuery()
            result.first()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun updateFuelPrice(fuelId: String, price: String, date: String?): Boolean {
        val query = "UPDATE fuel_price fp " +
                "JOIN available_fuel af ON fp.available_fuel_id = af.available_fuel_id " +
                "SET fp.rating = fp.rating + 1 " +
                "WHERE af.available_fuel_id = ? AND fp.price = ? AND fp.added_on = ?"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, fuelId.toLong())
            stm.setDouble(2, price.toDouble())
            stm.setDate(3, Date.valueOf(date))
            val result = stm.executeUpdate()
            result > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun addNewFuelPrice(fuelId: String, price: String?, date: String?): Boolean {
        val query = "INSERT INTO `fuel_price` (`available_fuel_id`, `price`, `added_on`) VALUES (?, ?, ?)"
        return try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, fuelId.toLong())
            stm.setString(2, price)
            stm.setDate(3, Date.valueOf(date))
            val result = stm.executeUpdate()
            result > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun getFavouriteGasStations(userId: Long): List<GasStation>? {
        val query = "SELECT * FROM favourite_gas_stations fgs " +
                "INNER JOIN gas_station gs ON fgs.station_id = gs.station_id " +
                "WHERE fgs.user_id = ?"
        val gasStations: MutableList<GasStation> = LinkedList()
        try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, userId)
            val result = stm.executeQuery()
            while (result.next()) {
                val gasStation = GasStation()
                gasStation.id = result.getInt("station_id").toLong()
                gasStation.name = result.getString("name")
                gasStation.address = result.getString("address")
                gasStation.isForElectricCars = result.getBoolean("electric_charging")
                gasStation.isForDisabledPeople = result.getBoolean("for_disabled_people")
                gasStation.longitude = result.getDouble("lng")
                gasStation.latitude = result.getDouble("lat")
                fillGasStationsOpeningHours(gasStation)
                gasStations.add(gasStation)
            }
            result.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return gasStations
    }

    fun isGasStationFavourite(userId: Long, gasStationId: Long): Boolean {
        val query = "SELECT * FROM favourite_gas_stations WHERE station_id = ? AND user_id = ?"
        var isFav = false
        try {
            val stm = db!!.prepareCall(query)
            stm.setLong(1, gasStationId)
            stm.setLong(2, userId)
            val result = stm.executeQuery()
            isFav = result.next()
            result.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return isFav
    }

    fun insertGasStationFavourite(userId: Long, gasStationId: Long) {
        val query = "INSERT INTO `favourite_gas_stations` (`user_id`, `station_id`) VALUES (?, ?);"
        try {
            db!!.prepareCall(query).use { stm ->
                stm.setLong(1, userId)
                stm.setLong(2, gasStationId)
                stm.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deleteGasStationFavourite(userId: Long, gasStationId: Long) {
        val query = "DELETE FROM favourite_gas_stations WHERE user_id = ? AND station_id = ?"
        try {
            db!!.prepareCall(query).use { stm ->
                stm.setLong(1, userId)
                stm.setLong(2, gasStationId)
                stm.execute()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}
