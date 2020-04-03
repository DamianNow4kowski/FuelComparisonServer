import com.fuelproject.database.DatabaseHelper
import com.fuelproject.handlers.ExampleHandler
import com.fuelproject.handlers.LoginHandler
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
        if (!databaseHelper.connect(Config.DB_ADDRESS, Config.DB_NAME, Config.DB_USER, Config.DB_PASSWORD)) {
            logger.error("Cannot connect to database!")
            exitProcess(1)
        }

        val server = Server(HttpServer.create(InetSocketAddress(Config.SERVER_PORT), 0), databaseHelper)
        server.addContext("/example", ExampleHandler())
        server.addContext("/login", LoginHandler())
        server.start()
    }
}
