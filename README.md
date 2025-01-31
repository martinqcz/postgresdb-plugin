# Postgresdb Gradle Plugin

Postgresdb Gradle plugin starts Docker container with Postgresql database 
instance and makes it available to Gradle tasks during the Gradle build.

The database instance can be used for generating source code from database 
schema. For example in combination with database migration tools such as 
Liquibase or Flyway and jOOQ code generation.

## Usage

```kotlin
plugins {
    id("com.qapil.postgresdb-gradle")
}

postgresdb {
    serviceName = "postgresdbService"   // service name that will be used to register to Gradle's shared services
    imageName = "postgres:16-alpine"    // docker image name of the Postgresql database
    username = "test"                   // database username
    password = "test"                   // database password
}

// register postgresdb build service
val postgresService = postgresdb.service()

tasks.named("taskThatNeedsDatabase") {
    usesService(postgresService)

    doFirst {
        // make sure the database is up and running and inject the connection details
        val postgres = postgresService.get()

        url = postgres.jdbcUrl
        user = postgres.username
        password = postgres.password
    }
}

```

### Example with Flyway and jOOQ

```kotlin
plugins {
    id("com.qapil.postgresdb-gradle")
    id("org.jooq.jooq-codegen-gradle")
    id("org.flywaydb.flyway")
}

// register postgresdb build service
val postgresService = postgresdb.service()

tasks.named<FlywayMigrateTask>("flywayMigrate") {
    finalizedBy("jooqCodegen")
    usesService(postgresService)

    doFirst {
        // make sure the database is up and running and inject the connection details
        val postgres = postgresService.get()

        url = postgres.jdbcUrl
        user = postgres.username
        password = postgres.password
    }
}

tasks.named<CodegenTask>("jooqCodegen") {
    dependsOn("flywayMigrate")
    usesService(postgresService)

    doFirst {
        // make sure the database is up and running and inject the connection details
        val postgres = postgresService.get()
        jooq {
            configuration {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc {
                    driver = "org.postgresql.Driver"
                    url = postgres.jdbcUrl
                    user = postgres.username
                    password = postgres.password
                }
            }
        }
    }

    inputs.files(flywayMigration)
}
```
