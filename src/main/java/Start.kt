import com.fuelproject.database.DatabaseHelper
import com.fuelproject.factories.HttpServerFactory.create
import com.fuelproject.handlers.ExampleHandler
import org.slf4j.LoggerFactory
import java.io.IOException
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

        val server = Server(create(Config.SERVER_PORT), databaseHelper)
        server.addContext("/example", ExampleHandler())
        server.start()
    }
}
