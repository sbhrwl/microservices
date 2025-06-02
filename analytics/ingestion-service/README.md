# Ingestion service

- Build: `mvn clean install`
- Run: `mvn spring-boot:run`
- Test
  - `http://localhost:8080/api/powerquality/generate`
  - It will send `~300 messages` to the `power-quality-queue`