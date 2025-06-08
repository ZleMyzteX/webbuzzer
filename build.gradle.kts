plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("com.diffplug.spotless") version "7.0.3"
}

group = "er.codes.web"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.netty)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

jib {
    from {
        image = "gcr.io/distroless/java21-debian12"
    }
    to {
        image = "localhost:5000/web-buzzer"
    }
    container {
        mainClass = "er.codes.web.ApplicationKt"
        ports = listOf("8080")
    }
    setAllowInsecureRegistries(true)
}