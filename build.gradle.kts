import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.6.10"
    id("java")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("maven-publish")
}

group = "com.levels"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core:$ktor_version")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
    systemProperty("gradle.build.dir", project.buildDir)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

ktlint {
    disabledRules.set(mutableListOf("no-wildcard-imports"))
    filter {
        exclude("**/generated/**")
        exclude { element -> element.file.path.contains("generated/") }
        include("**/kotlin/**")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/karlazzampersonal/ktor-vault")
            credentials {
                username = System.getenv("GH_USER")
                password = System.getenv("GH_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
