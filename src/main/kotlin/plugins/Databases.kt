package com.lemonpie.plugins

import com.lemonpie.dotenv
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabases() {

    Database.connect(
        url="${dotenv["DATABASE_URL"]}/url_shortener",
        // Needs a postgres database!
        driver="org.postgresql.Driver",
        user=dotenv["DATABASE_USER"],
        password=dotenv["DATABASE_PASSWORD"]
    )
}