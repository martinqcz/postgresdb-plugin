plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "com.qapil.gradle"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    val testcontainersVersion = "1.19.7"
    implementation("org.testcontainers:testcontainers:$testcontainersVersion")
    implementation("org.testcontainers:postgresql:$testcontainersVersion")

}

gradlePlugin {
    plugins {
        create("postgresdb") {
            id = "com.qapil.postgresdb-gradle"
            implementationClass = "com.qapil.gradle.postgresdb.PostgresDbPlugin"
        }
    }
}
