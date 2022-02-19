package com.levels.vault.api

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import java.text.DateFormat

fun configureHttpClient(engine: HttpClientEngine = CIO.create()): HttpClient {
    return HttpClient(engine) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                enable(SerializationFeature.INDENT_OUTPUT)
                dateFormat = DateFormat.getDateInstance()
            }
        }
    }
}
