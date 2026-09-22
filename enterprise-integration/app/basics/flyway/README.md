# Flyway
- [Overview](#overview)
- [1. Add Flyway to Gradle](#1-add-flyway-to-gradle)
- [2. Create the migration directory](#2-create-the-migration-directory)
- [3. Configure Flyway](#3-configure-flyway)
- [4. Your application can also run migrations automatically](#4-your-application-can-also-run-migrations-automatically)
- [5. For CI/CD](#5-for-cicd)
## Overview
- Flyway is a very clean way to automate database migrations.
```text
Git
 │
 ├── application code
 │
 └── db/migration
       ├── V1__initial_schema.sql
       ├── V2__add_customer_table.sql
       ├── V3__add_indexes.sql
       └── V4__alter_xxx.sql
                │
                ▼
             Flyway
                │
                ▼
            PostgreSQL
```
## 1. Add Flyway to Gradle
- For a Gradle project using `build.gradle`:
```gradle
plugins {
  id 'java'
  id 'org.flywaydb.flyway' version '11.0.0'
}
```
- Then add the PostgreSQL driver:
```gradle
dependencies {
  implementation 'org.postgresql:postgresql:42.7.4'
}
```
- Flyway keeps a `flyway_schema_history` table in the database and automatically knows which scripts have already been executed
## 2. Create the migration directory
- Put your SQL files here:
```text
src/
└── main/
    └── resources/
        └── db/
            └── migration/
                ├── V1__initial_schema.sql
                ├── V2__create_customer_table.sql
                └── V3__add_customer_index.sql
```
## 3. Configure Flyway
- In `build.gradle`:
```gradle
flyway {
  url = 'jdbc:postgresql://localhost:5432/mydb'
  user = 'postgres'
  password = 'postgres'
  locations = ['classpath:db/migration']
}
```
- Now you can run:
  - `./gradlew flywayMigrate`
  - `gradlew.bat flywayMigrate`
- Flyway expects this naming convention:
  - `V<version>__<description>.sql`
- For example:
  - `V1__initial_schema.sql`
  - `V2__create_customer_table.sql`
  - `V3__add_customer_index.sql`
  - `V4__add_customer_status.sql`
- Don't edit an already-applied migration. Create a new one.
- So if V2 is already deployed:
  - `V2__create_customer_table.sql` don't modify
  - `V3__add_customer_email.sql` create new migration
- Flyway will:
  - Connect to PostgreSQL
  - Look at `flyway_schema_history`
  - Find migrations that haven't run
  - Execute them in order
  - Record them as successful
## 4. Your application can also run migrations automatically
- If this is a Spring Boot application, you can have Flyway run when the application starts.
- Add:
```gradle
dependencies {
  implementation 'org.flywaydb:flyway-core'
  implementation 'org.flywaydb:flyway-database-postgresql'
  runtimeOnly 'org.postgresql:postgresql'
}
```
- Then:
```text
Application starts
       ↓
Flyway starts
       ↓
Check flyway_schema_history
       ↓
Run pending migrations
       ↓
Application starts normally
```
- This is often the simplest approach for a service.
## 5. For CI/CD
- Since you're already using GitLab, this gets particularly useful.
- You can have:
```text
Developer
   ↓
Create V5__xxx.sql
   ↓
Git commit
   ↓
GitLab CI
   ↓
Build / Test
   ↓
Flyway migrate
   ↓
PostgreSQL
```
- For example:
```yaml
database-migration:
  stage: deploy
  script:
    - ./gradlew flywayMigrate
```
