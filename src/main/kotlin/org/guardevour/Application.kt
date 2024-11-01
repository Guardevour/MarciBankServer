package org.guardevour

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.guardevour.dao.Database
import org.guardevour.plugins.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    Database.init()
    configureSecurity()
    configureRouting()
}
