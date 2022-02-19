# Ktor vault plugin
![Publish](https://github.com/karlazzampersonal/ktor-vault/actions/workflows/deploy.yml/badge.svg?branch=main)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)

A Ktor installable plugin for vault that allows you to inject vault secrets into your project. 

The vault token is the only supported login mechanism

## Usage
<details><summary>Set up in Kotlin Gradle:</summary>

```kotlin
repositories {
    mavenCentral()
    // Need a GH access token with read package scope
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/karlazzampersonal/ktor-vault")
        credentials {
            username = props.getProperty("USERNAME")
            password = props.getProperty("TOKEN")
        }
    }
}

dependencies {
    implementation("com.levels:ktor-vault:$ktor_vault_version")
}
```
</details>

First, Add the feature to your Application module

```kotlin
// Get the vault token and engine from environment variables
val env = System.getenv("ENV")
install(Vault) {
    token = System.getenv("VAULT_TOKEN")
    engine =  System.getenv("ENGINE")
    // Specify all the secret paths to look in
    secretPaths = listOf(
        "shared/".plus(env),
        "my-service/".plus(env)
    )
} 

```
Create an env singleton and fetch your secrets via the secret name 
```kotlin

object Env {
    val awsRegion: String = Vault.getSecret("aws.region")

    val kafkaBootstrapServer: String = Vault.getSecret("kafka.bootstrap-server")
   
    // and your other secrets .....
}
```
Use your env variables in another part of the code
```kotlin
    install(Kafka) {
        kafka { 
            bootstrapServer = Env.kafkaBootstrapServer
        } 
    }
```

## License
This project is licensed under the Apache 2.0 license

## Contributing
We welcome any contributions, please submit an issue or PR.
