package com.mytemp

import KonfApplicationConfig
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import initKoin
import io.ktor.jackson.*
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>) {
    val config = loadConfig()
    val cmdEnv = buildAppEngineEnv(config)
    initKoin(config)
    embeddedServer(Netty, cmdEnv).start()
}

@KtorExperimentalAPI
fun buildAppEngineEnv(config: Config): ApplicationEngineEnvironment {
    return applicationEngineEnvironment {
        this.config = KonfApplicationConfig(config)
        connector {
            port = config[Ktor.Deployment.port]
        }
    }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate { if (it.name == "test" && it.password == "password") UserIdPrincipal(it.name) else null }
        }
    }
    install(Routing) { route() }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

val topLevelConfigs = arrayOf(
    Ktor
)

object Ktor : ConfigSpec() {
    object Deployment : ConfigSpec() {
        val port by required<Int>()
    }

    object Application : ConfigSpec() {
        val modules by required<Array<String>>()
    }
}

fun loadConfig(): Config {
    return Config { topLevelConfigs.forEach(::addSpec) }
        .from.hocon.resource("application.conf")
}

