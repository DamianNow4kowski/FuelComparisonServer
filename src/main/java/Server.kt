import com.fuelproject.database.DatabaseHelper
import com.fuelproject.handlers.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory

class Server internal constructor(private val httpServer: HttpServer, private var databaseHelper: DatabaseHelper) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        httpServer.start()
        logger.info("Started server!")
    }

    fun addContext(path: String?, httpHandler: HttpHandler) {
        httpHandler.databaseHelper(databaseHelper)
        httpServer.createContext(path, httpHandler)
    }
}
