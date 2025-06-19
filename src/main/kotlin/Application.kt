package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain.main
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry


fun main(args: Array<String>) {
    main(args)
}

fun Application.module() {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    configureMonitoring(prometheusRegistry)
    configureSockets(prometheusRegistry)
    configureRouting()
}
