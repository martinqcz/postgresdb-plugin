package com.qapil.gradle.postgresdb

import javax.inject.Inject
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Internal
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

abstract class PostgresDbService : BuildService<PostgresDbService.Parameters>, AutoCloseable {

    interface Parameters : BuildServiceParameters {
        val imageName: Property<String>
        val username: Property<String>
        val password: Property<String>
    }

    @Inject
    private val logger = Logging.getLogger(PostgresDbService::class.java)

    @Internal
    private val container: PostgreSQLContainer

    @get:Internal
    val jdbcUrl: String
        get() = container.jdbcUrl

    @get:Internal
    val username: String
        get() = container.username

    @get:Internal
    val password: String
        get() = container.password

    @get:Internal
    val databaseName: String
        get() = container.databaseName

    init {
        val imageName = parameters.imageName.get()
        val image = DockerImageName.parse(imageName)

        logger.quiet("[PostgresService] Starting container for \"$imageName...")

        container = PostgreSQLContainer(image)
            .withUsername(parameters.username.get())
            .withPassword(parameters.password.get())
        container.start()

        logger.quiet(
            "[PostgresService] Container is running:" +
                " ${container.containerName}[${container.containerId}] (${container.dockerImageName})"
        )
    }

    override fun close() {
        logger.quiet(
            "[PostgresService] Stopping container" +
                " ${container.containerName}[${container.containerId}] (${container.dockerImageName})..."
        )
        container.stop()
    }
}

