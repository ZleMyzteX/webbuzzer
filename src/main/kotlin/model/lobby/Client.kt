package er.codes.web.model.lobby

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val clientId: String,
)