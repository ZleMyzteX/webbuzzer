package er.codes.web.model.lobby

import kotlinx.serialization.Serializable

@Serializable
data class Lobby(
    val id: String,
    val displayName: String,
    val admin: Client,
    val state: LobbyState = LobbyState.WAITING,
    val clients: MutableList<Client> = mutableListOf()
)