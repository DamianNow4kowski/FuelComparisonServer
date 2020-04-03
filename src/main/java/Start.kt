import com.fuelproject.database.DatabaseHelper
import com.fuelproject.factories.HttpServerFactory.create
import com.fuelproject.handlers.ExampleHandler
import org.slf4j.LoggerFactory
import java.io.IOException

object Start {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val server = Server(create(8000))
        server.addContext("/example", ExampleHandler())
        if (DatabaseHelper.connect("localhost", "mob_java", "root", "")) {
            server.start()
        } else logger.error("Cannot connect to database!")
    }
}
