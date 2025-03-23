import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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


tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

dependencies {
    val testcontainersVersion = "1.19.7"
    implementation("org.testcontainers:testcontainers:$testcontainersVersion")
    implementation("org.testcontainers:postgresql:$testcontainersVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    website = "https://github.com/martinqcz/postgresdb-plugin"
    vcsUrl = "https://github.com/martinqcz/postgresdb-plugin"
    plugins {
        create("postgresdb") {
            id = "com.qapil.postgresdb-gradle"
            displayName = "Postgresdb Plugin"
            description = "Postgresql database container available for Gradle build tasks. " +
                "Can be used for generating source code from database schema."
            tags = listOf("postgres", "database", "testcontainers", "jooq")
            implementationClass = "com.qapil.gradle.postgresdb.PostgresDbPlugin"
        }
    }
}

tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    archiveClassifier = ""
}
