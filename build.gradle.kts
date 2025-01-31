plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
    id("com.gradleup.shadow") version "8.3.5"
    signing
}

group = "com.qapil.gradle"
version = "1.0"

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
    website = "https://github.com/martinqcz/postgresdb-plugin"
    vcsUrl = "https://github.com/martinqcz/postgresdb-plugin"
    plugins {
        create("postgresdb") {
            id = "com.qapil.postgresdb-gradle"
            displayName = "Postgresdb Plugin"
            description = "Postgresql database container available for Gradle build tasks." +
                "Can be used for generating source code from database schema."
            tags = listOf("postgres", "database", "testcontainers", "jooq")
            implementationClass = "com.qapil.gradle.postgresdb.PostgresDbPlugin"
        }
    }
}

tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    archiveClassifier = ""
}
