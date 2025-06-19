package er.codes.web.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault

@Serializable
data class TimestampJson(
    val timestampNs: Long,
    val clientId: String,
) {
    fun toLocalDateTime(): LocalDateTime {
        val seconds = this.timestampNs / 1_000_000_000
        val nanos = (this.timestampNs % 1_000_000_000).toInt()

        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(seconds, nanos.toLong()),
            systemDefault()
        )
    }

    fun toStringTimeStamp(): String {
        return this.toLocalDateTime().toString()
    }
}