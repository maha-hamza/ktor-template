package com.mytemp

import TestController
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import org.koin.ktor.ext.get as inject
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post

fun Routing.get(path: String, func: suspend (ApplicationCall) -> Unit) = get(path) { func(call) }
fun Routing.post(path: String, func: suspend (ApplicationCall) -> Unit) = post(path) { func(call) }
fun Routing.patch(path: String, func: suspend (ApplicationCall) -> Unit) = patch(path) { func(call) }
fun Routing.delete(path: String, func: suspend (ApplicationCall) -> Unit) = delete(path) { func(call) }

fun Routing.route() {

    val testController = inject<TestController>()

    get("/") { call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain) }
    get("/json/jackson") { call.respond(mapOf("hello" to "world")) }
    get("/say-hello", testController::sayHelloMaha)

    authenticate("myBasicAuth") {
        get("/protected/route/basic") {
            val principal = call.principal<UserIdPrincipal>()!!
            call.respondText("Hello ${principal.name}")
        }
    }
}