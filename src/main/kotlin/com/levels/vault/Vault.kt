package com.levels.vault

import com.levels.vault.api.Logical
import io.ktor.application.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("com.levels.ktor.vault.VaultFeature")

data class VaultConfiguration(
    var address: String = "http://127.0.0.1:8200",
    var token: String = "",
    var engine: String = "",
    var secretPaths: List<String> = listOf(),
)

class Vault(configuration: VaultConfiguration) {
    val address = configuration.address
    val token = configuration.token
    val engine = configuration.engine
    val secretPaths = configuration.secretPaths

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, VaultConfiguration, Vault> {
        override val key = AttributeKey<Vault>("Vault")
        private val secretsMap: MutableMap<String, String> = mutableMapOf()

        override fun install(pipeline: ApplicationCallPipeline, configure: VaultConfiguration.() -> Unit): Vault {
            val config = VaultConfiguration().apply(configure)
            val vaultFeature = Vault(config)
            val logical = Logical(config)

            log.info("Init vault for Address: ${vaultFeature.address} Engine: ${vaultFeature.engine}")
            log.debug("Token ${vaultFeature.token}")
            log.info("Secret paths:")
            for (path in vaultFeature.secretPaths) {
                log.info("Path: $path")
            }

            runBlocking {
                for (secret in config.secretPaths) {
                    val vaultResp = logical.read(secret)
                    vaultResp?.let {
                        val secrets: Map<String, String> = vaultResp.data.data
                        secretsMap.putAll(secrets)
                    }
                }
            }

            return vaultFeature
        }

        fun getSecret(name: String): String {
            return secretsMap[name]!!
        }
    }
}
