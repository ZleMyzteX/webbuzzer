package er.codes.web.model.lobby

import kotlinx.serialization.Serializable

@Serializable
enum class LobbyState {
    WAITING,
    STARTED,
    DONE
}