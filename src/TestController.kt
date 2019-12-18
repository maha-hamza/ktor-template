import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.koin.core.KoinComponent

class TestController : KoinComponent {

    suspend fun sayHelloMaha(call: ApplicationCall) {
        return call.respond("Hello Maha")
    }
}