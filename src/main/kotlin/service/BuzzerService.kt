package er.codes.web.service

import er.codes.web.model.TimestampJson
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class BuzzerService {
    private val log = LoggerFactory.getLogger(BuzzerService::class.java)

    fun parseMessage(message: String): TimestampJson? =
        try {
            Json.decodeFromString<TimestampJson>(message)
        } catch (e: Exception) {
            log.error("Couldn't parse JSON.")
            log.error("Message: $message")
            log.error("Exception: ${e.message}")
            null
        }

}