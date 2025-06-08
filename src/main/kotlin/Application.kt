package er.codes.web

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain.main
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun main(args: Array<String>) {
    main(args)
}

fun Application.module() {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    configureMonitoring(prometheusRegistry)
    configureSockets(prometheusRegistry)
    configureRouting()
}
