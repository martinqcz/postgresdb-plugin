# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin (`com.qapil.postgresdb-gradle`) that starts a Docker container with a PostgreSQL database instance and makes it available to Gradle tasks during the build. The primary use case is generating source code from database schemas using tools like Liquibase/Flyway + jOOQ.

The plugin uses Testcontainers to manage PostgreSQL Docker containers and exposes them as a Gradle BuildService for safe concurrent access.

## Build Commands

```bash
# Build the plugin
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests PostgresDbPluginTest

# Run a specific test method
./gradlew test --tests PostgresDbPluginTest."plugin registers postgresdb extension"

# Publish to Gradle Plugin Portal (requires credentials)
./gradlew publishPlugins

# Generate signing key (for plugin publishing)
./gradlew sign
```

## Architecture

### Core Components

1. **PostgresDbPlugin** (`src/main/kotlin/.../PostgresDbPlugin.kt`): The main plugin class that registers the `postgresdb` extension with Gradle projects. Sets default values for service name, image, username, and password.

2. **PostgresDbService** (`src/main/kotlin/.../PostgresDbService.kt`): A Gradle BuildService that manages the PostgreSQL container lifecycle. This service:
   - Starts a PostgreSQL container using Testcontainers on initialization
   - Exposes `jdbcUrl`, `username`, `password`, and `databaseName` properties
   - Stops the container when the service is closed (end of build)
   - Uses `maxParallelUsages = 1` to ensure only one task uses the database at a time

3. **PostgresDbExtension**: The DSL extension that allows users to configure:
   - `serviceName`: Name for the Gradle shared service registration
   - `imageName`: Docker image (e.g., "postgres:16-alpine")
   - `username`: Database username
   - `password`: Database password
   - `service()`: Method to get a Provider of the PostgresDbService

### Key Design Patterns

- **Gradle BuildService**: Ensures the PostgreSQL container is shared across the build and properly cleaned up. The service is registered lazily via `registerIfAbsent()`.
- **Testcontainers**: Handles Docker container lifecycle, port mapping, and connection details automatically.
- **Provider API**: Uses Gradle's lazy configuration to defer service creation until needed.

### Dependency Management

- Target JVM: Java 11 (both Kotlin and Java compilation)
- Key dependencies: Testcontainers (PostgreSQL module), Apache Commons Compress (security patch)
- Dependencies are NOT relocated/bundled in the plugin JAR (see commented-out Shadow plugin configuration)

## Testing

Tests use:
- JUnit 5 (Jupiter)
- AssertJ for fluent assertions
- Gradle TestKit (available but not currently used in existing tests)

The current test suite covers plugin registration and extension configuration, but does not test the actual container lifecycle (which would require Docker to be available).
