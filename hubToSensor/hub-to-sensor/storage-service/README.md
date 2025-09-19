# Storage service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
## Properties
- [application.properties](src/main/resources/application.properties)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test
- Push data to PostGreSQL database
  - `POST`: `http://localhost:8083/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30
    }
    ```
```
C:\Users\sabharwalr>psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# select * from control_requests;
 id | duration | operation | relay_number | sensor_id  |  status
----+----------+-----------+--------------+------------+----------
  1 |       30 | DIRECT-ON |            2 | sensor-001 | Received
```