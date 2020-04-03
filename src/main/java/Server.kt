import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory

class Server internal constructor(private val httpServer: HttpServer) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        httpServer.start()
        logger.info("Started server!")
    }

    fun addContext(path: String?, handler: HttpHandler?) {
        httpServer.createContext(path, handler)
    }

}
