package com.qapil.gradle.postgresdb

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceRegistration
import org.gradle.api.services.BuildServiceSpec
import org.assertj.core.api.Assertions.assertThat

class PostgresDbServiceTest {
    private lateinit var project: Project
    private lateinit var service: PostgresDbService
    
    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }
    
    @Test
    fun `service starts postgres container with correct configuration`() {
        // Register and create service
        val registration = project.gradle.sharedServices
            .registerIfAbsent("testPostgresService", PostgresDbService::class.java) {
                parameters {
                    imageName.set("postgres:16-alpine")
                    username.set("testuser")
                    password.set("testpass")
                }
                maxParallelUsages.set(1)
            }
        
        service = registration.get()
        
        try {
            // Verify service configuration
            assertThat(service.username).isEqualTo("testuser").withFailMessage("Username should match configured value")
            assertThat(service.password).isEqualTo("testpass").withFailMessage("Password should match configured value")
            assertThat(service.jdbcUrl).startsWith("jdbc:postgresql://").withFailMessage("JDBC URL should be properly formatted")
            assertThat(service.databaseName).isNotNull().withFailMessage("Database name should be set")
        } finally {
            service.close()
        }
    }
    
    @Test
    fun `service provides valid connection parameters`() {
        // Register and create service
        val registration = project.gradle.sharedServices
            .registerIfAbsent("testPostgresService2", PostgresDbService::class.java, {
                parameters {
                    imageName.set("postgres:16-alpine")
                    username.set("testuser")
                    password.set("testpass")
                }
                maxParallelUsages.set(1)
            })
        
        service = registration.get()
        
        try {
            // Verify connection parameters
            assertThat(service.jdbcUrl)
                .isNotNull()
                .withFailMessage("JDBC URL should not be null")
                .contains("localhost")
                .withFailMessage("JDBC URL should point to localhost")
                .contains(service.databaseName)
                .withFailMessage("JDBC URL should contain database name")
        } finally {
            service.close()
        }
    }
} 