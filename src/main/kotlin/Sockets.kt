package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.micrometer.core.instrument.Gauge
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.Instant
import java.time.LocalDateTime
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
                        val (clientId, timestamp) = try {
                            val json = parseToJsonElement(message).jsonObject
                            val id = json["clientId"]?.jsonPrimitive?.content ?: "unknown"
                            val ts = json["timestampNs"]?.jsonPrimitive?.longOrNull
                            id to ts
                        } catch (e: Exception) {
                            log.error("Couldn't parse JSON.")
                            log.error("Message: $message")
                            log.error("Exception: ${e.message}")
                            "unknown" to null
                        }

                        val humanReadableTimestamp = timestamp?.let {
                            val seconds = it / 1_000_000_000
                            val nanos = (it % 1_000_000_000).toInt()
                            LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(seconds, nanos.toLong()),
                                java.time.ZoneId.systemDefault()
                            ).toString()
                        } ?: "unknown"

                        log.info("Received timestamp from clientId=$clientId: $message (timestamp: $humanReadableTimestamp)")
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

