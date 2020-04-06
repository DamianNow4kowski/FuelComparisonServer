import com.fuelproject.database.DatabaseHelper
import com.fuelproject.handlers.*
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import kotlin.system.exitProcess


object Start {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val databaseHelper = DatabaseHelper

        initDatabase(databaseHelper)

        val server = Server(HttpServer.create(InetSocketAddress(Config.SERVER_PORT), 0), databaseHelper)
        server.addContext("/login", LoginHandler())
        server.addContext("/register", RegisterHandler())
        server.addContext("/remindPassword", RemindPasswordHandler())
        server.addContext("/addStation", AddGasStationHandler())
        server.addContext("/retrieveStations", RetrieveGasStationsHandler())
        server.addContext("/getFuels", FuelsHandler())
        server.addContext("/getComments", CommentsHandler())
        server.addContext("/addFuelPrice", AddFuelPriceHandler())
        server.start()
    }

    private fun initDatabase(databaseHelper: DatabaseHelper) {
        if (!databaseHelper.connect(Config.DB_ADDRESS, Config.DB_USER, Config.DB_PASSWORD)) {
            logger.error("Cannot connect to database!")
            exitProcess(1)
        }

        if (databaseHelper.selectDatabase(Config.DB_NAME)) {
            logger.info("Clear DB on start: ${Config.CLEAR_DB_ON_START}")
            if (Config.CLEAR_DB_ON_START) {
                databaseHelper.dropDatabase(Config.DB_NAME)
                databaseHelper.createDatabase(Config.DB_NAME)
                databaseHelper.fillDatabase(Config.DB_SQL_FILE, Config.DB_NAME)
                logger.info("Drop and create from file completed!")
            } else {
                logger.info("Selected existing database")
            }
        } else {
            databaseHelper.createDatabase(Config.DB_NAME)
            databaseHelper.fillDatabase(Config.DB_SQL_FILE, Config.DB_NAME)
        }
    }
}
