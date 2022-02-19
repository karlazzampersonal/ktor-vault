package com.levels.vault.api

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.levels.vault.VaultConfiguration
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import org.slf4j.LoggerFactory

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Secret(
    val requestId: String,
    val leaseId: String,
    val renewable: Boolean,
    val leaseDuration: Int,
    val data: Data,
    val wrapInfo: Any?,
    val warnings: Any?,
    val auth: Any?
)

data class Data(
    val data: Map<String, String>,
    val metadata: Map<String, String>
)

class Logical(private val conf: VaultConfiguration) {
    private val log = LoggerFactory.getLogger(Logical::class.java)

    @OptIn(InternalAPI::class)
    suspend fun read(path: String): Secret? {
        val client = configureHttpClient()

        try {
            val secret: Secret = client.request<HttpStatement>(conf.address.plus("/v1/").plus(conf.engine).plus("/data/").plus(path)) {
                method = HttpMethod.Get
                headers {
                    append("X-Vault-Token", conf.token)
                }
            }.receive()

            client.close()
            return secret
        } catch (cause: Throwable) {
            log.error("Error grabbing secret from path $path")
        }
        return null
    }
}
