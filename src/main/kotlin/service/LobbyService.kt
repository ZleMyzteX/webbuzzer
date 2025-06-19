package er.codes.web.service

import er.codes.web.model.lobby.Client
import er.codes.web.model.lobby.Lobby
import org.slf4j.LoggerFactory
import java.util.UUID.randomUUID

object LobbyService{
    private val log = LoggerFactory.getLogger(LobbyService::class.java)

    private val lobbies = mutableListOf<Lobby>()


    fun getAllLobbies(): List<Lobby> {
        log.info("Getting all lobbies")
        return lobbies
    }

    fun createLobby(displayName: String, admin: String): Lobby {
        val lobby = Lobby(
            id = randomUUID().toString(),
            displayName = displayName,
            admin = Client(clientId = admin)
        )
        log.info("Adding lobby: $lobby")
        lobbies.add(lobby)
        return lobby
    }

    fun addClientToLobby(lobbyId: String, client: Client): Lobby? {
        log.info("Adding client $client to lobby with ID: $lobbyId")
        val lobby = lobbies.find { it.id == lobbyId }
        return if (lobby != null) {
            lobby.clients.add(client)
            log.info("Updated lobby: $lobby")
            lobby
        } else {
            log.warn("Lobby with ID $lobbyId not found")
            null
        }
    }


}