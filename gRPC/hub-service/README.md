# Hub service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
- [Verification at InfluxDB](#verification-at-influxdb)
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
- DB verification
```sql
SELECT * FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE sensor_id = 'sensor123';

SELECT COUNT(*) FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE email = 'user@example.com';
```