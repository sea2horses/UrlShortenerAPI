plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.exposed.plugin") version "1.3.0"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "com.lemonpie"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

val dbUrl = env.fetch("DATABASE_URL") + "/url_shortener"
val dbUser = env.fetch("DATABASE_USER")
val dbPassword = env.fetch("DATABASE_PASSWORD")

exposed {
    migrations {
        databaseUrl.set(dbUrl)
        databaseUser.set(dbUser)
        databasePassword.set(dbPassword)
        tablesPackage.set("com.lemonpie.mapping")
    }
}

// Generate migration scripts before the build task
tasks.named("build") {
    dependsOn("generateMigrations")
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.csrf)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.openapi)
    implementation(ktorLibs.server.routingOpenapi)
    implementation(ktorLibs.server.statusPages)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.h2database.h2)
    implementation(libs.h2database.r2dbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.datetime)
    // Source: https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.11")
    // Source: https://mvnrepository.com/artifact/at.favre.lib/bcrypt
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation(libs.logback.classic)
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
