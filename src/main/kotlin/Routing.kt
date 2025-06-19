package er.codes.web

import er.codes.web.model.lobby.Client
import er.codes.web.service.LobbyService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.time.Instant

fun Application.configureRouting() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost() // TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        json(
            Json { prettyPrint = true }
        )
    }

    intercept(ApplicationCallPipeline.Setup) {
        call.response.headers.append(
            "Content-Security-Policy",
            "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';"
        )
    }

    routing {
        staticResources("/", "static") {
            default("index.html")
        }

        get("/api/server-time") {
            log.info("Received request for server time")
            call.respond(mapOf("now" to Instant.now().toEpochMilli()))
        }

        get("/api/lobbies") {
            log.info("Received request for lobbies")
            call.respond(LobbyService.getAllLobbies())
        }

        post("/api/lobby") {
            log.info("Received lobby creation request")
            val params = call.receiveParameters()
            val displayName = params["displayName"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Missing displayName"
            )
            val clientId = params["clientId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Missing clientId"
            )

            val lobby = LobbyService.createLobby(displayName, clientId)
            call.respond(lobby)
        }

        post("/api/lobby/{lobbyId}/register") {
            val lobbyId = call.parameters["lobbyId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing lobbyId")
            // TODO: users could register for other users, fix
            val params = call.receiveParameters()
            val clientId = params["clientId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing clientId")

            val lobby = LobbyService.addClientToLobby(lobbyId, Client(clientId))

            if (lobby != null) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Conflict, "Already registered or lobby full")
            }
        }
    }
}
