package com.qapil.gradle.postgresdb

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

class PostgresDbPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("postgresdb", PostgresDbExtensionImpl::class.java, project).apply {
            serviceName.convention("postgresdbService")
            imageName.convention("postgres:16-alpine")
            username.convention("test")
            password.convention("test")
        }
    }
}

interface PostgresDbExtension {
    val serviceName: Property<String>
    val imageName: Property<String>
    val username: Property<String>
    val password: Property<String>
    fun service(): Provider<PostgresDbService>
}

abstract class PostgresDbExtensionImpl(
    private val project: Project,
) : PostgresDbExtension {
    override fun service(): Provider<PostgresDbService> =
        project.gradle.sharedServices.registerIfAbsent(serviceName.get(), PostgresDbService::class.java) {
            parameters.imageName.set(imageName.get())
            parameters.username.set(username.get())
            parameters.password.set(password.get())
            maxParallelUsages.set(1)
        }
}
