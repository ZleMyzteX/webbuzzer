package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    val activeConnections = AtomicInteger(0)

    routing {
        webSocket("/timestamps") {
            val clientIp = call.request.origin.remoteHost
            val connectionId = activeConnections.incrementAndGet()

            log.info("Client connected: IP=$clientIp, ConnectionId=$connectionId, Active connections=${activeConnections.get()}")

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        val clientId = try {
                            val json = parseToJsonElement(message).jsonObject
                            json["clientId"]?.jsonPrimitive?.content ?: "unknown"
                        } catch (e: Exception) {
                            log.error("Couldn't parse JSON.")
                            log.error("Message: $message")
                            log.error("Exception: ${e.message}")
                        }

                        log.info("Received timestamp from clientId=$clientId: $message ns")
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

