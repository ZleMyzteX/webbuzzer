package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.http.content.default
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.micrometer.prometheus.*
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    routing {
        staticResources("/", "static") {
            default("index.html")
        }

    }
}
