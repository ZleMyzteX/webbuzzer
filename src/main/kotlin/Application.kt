package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain.main

fun main(args: Array<String>) {
    main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSockets()
    configureRouting()
}
