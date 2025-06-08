package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.micrometer.prometheus.*
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureMonitoring(prometheusRegistry: PrometheusMeterRegistry) {
    install(MicrometerMetrics) {
        registry = prometheusRegistry
        // ...
    }
    routing {
        get("/metrics-micrometer") {
            call.respond(prometheusRegistry.scrape())
        }
    }
}
