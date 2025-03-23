package com.qapil.gradle.postgresdb

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.assertj.core.api.Assertions.assertThat

class PostgresDbPluginTest {
    
    @Test
    fun `plugin registers postgresdb extension`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.qapil.postgresdb-gradle")
        
        val extension = project.extensions.findByType(PostgresDbExtension::class.java)
        assertNotNull(extension, "PostgresDb extension should be registered")
    }
    
    @Test
    fun `extension has default values`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.qapil.postgresdb-gradle")
        
        val extension = project.extensions.getByType(PostgresDbExtension::class.java)
        assertThat(extension.serviceName.get())
            .isEqualTo("postgresdbService")
            .withFailMessage("Default service name should be 'postgresdbService'")
        assertThat(extension.imageName.get())
            .isEqualTo("postgres:16-alpine")
            .withFailMessage("Default image name should be 'postgres:16-alpine'")
        assertThat(extension.username.get())
            .isEqualTo("test")
            .withFailMessage("Default username should be 'test'")
        assertThat(extension.password.get())
            .isEqualTo("test")
            .withFailMessage("Default password should be 'test'")
    }
    
    @Test
    fun `can configure extension values`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.qapil.postgresdb-gradle")
        
        val extension = project.extensions.getByType(PostgresDbExtension::class.java)
        extension.serviceName.set("customService")
        extension.imageName.set("postgres:15")
        extension.username.set("custom")
        extension.password.set("secret")
        
        assertThat(extension.serviceName.get())
            .isEqualTo("customService")
            .withFailMessage("Service name should be configurable")
        assertThat(extension.imageName.get())
            .isEqualTo("postgres:15")
            .withFailMessage("Image name should be configurable")
        assertThat(extension.username.get())
            .isEqualTo("custom")
            .withFailMessage("Username should be configurable")
        assertThat(extension.password.get())
            .isEqualTo("secret")
            .withFailMessage("Password should be configurable")
    }
} 