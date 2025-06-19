package er.codes.web

import er.codes.web.service.BuzzerService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.micrometer.core.instrument.Gauge
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(prometheusRegistry: PrometheusMeterRegistry) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    val activeConnections = AtomicInteger(0)
    val buzzerService = BuzzerService()
    Gauge.builder("active_websocket_connections") { activeConnections.get() }
        .description("Number of active WebSocket connections")
        .register(prometheusRegistry)

    routing {
        webSocket("/timestamps") {
            val clientIp = call.request.origin.remoteHost
            val connectionId = activeConnections.incrementAndGet()

            log.info("Client connected: IP=$clientIp, ConnectionId=$connectionId, Active connections=${activeConnections.get()}")

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        val parsed = buzzerService.parseMessage(message)
                        val readableTimeStamp = parsed?.toStringTimeStamp()

                        log.info("Received timestamp from clientId=${parsed?.clientId}: $message (timestamp: $readableTimeStamp)")
                    }
                }
            } catch (e: Exception) {
                log.error("WebSocket error (ConnectionId=$connectionId): ${e.localizedMessage}")
            } finally {
                val currentActive = activeConnections.decrementAndGet()
                log.info("Client disconnected: IP=$clientIp, ConnectionId=$connectionId, Active connections=$currentActive")
            }
        }
    }
}

