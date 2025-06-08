# Sensor simulator
- Build the project:
```bash
mvn clean install
```
- Run the application:
```bash
mvn spring-boot:run
```

## Test
- `POST` `http://localhost:9084/simulate`
  - payload: `raw`: `TestCommand`
  - `Simulated sensor received payload of length 11`

