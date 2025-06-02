# Ingestion service
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
- [Verification at InfluxDB](#verification-at-influxdb)
## Properties
- [application.yml](src/main/resources/application.yml)
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
- Publish sensor data to ActiveMQ  `http://localhost:8080/api/powerquality/generate`
  - It will send `~300 messages` to the `power-quality-queue`
## Verification at InfluxDB
### Single measurement
- Basic queries 
```sql
-- 1. See all field keys (e.g., voltage, current, etc.)
SHOW FIELD KEYS FROM power_quality

-- 2. See all tag keys (e.g., meter_id, obis_code, phase)
SHOW TAG KEYS FROM power_quality

-- 3. Get a few sample records
SELECT * FROM power_quality LIMIT 10

-- 4. See all meter IDs stored
SHOW TAG VALUES FROM power_quality WITH KEY = "meter_id"
```
- Analytical queries 
```sql
-- 1. Average voltage per meter
SELECT MEAN(voltage) FROM power_quality GROUP BY meter_id

-- 2. Voltage over time (last 1 hour)
SELECT voltage FROM power_quality WHERE time > now() - 1h

-- 3. Max current per phase
SELECT MAX(current) FROM power_quality GROUP BY phase

-- 4. Power factor trends for a specific meter
SELECT "power_factor" FROM power_quality WHERE meter_id = 'METER-001'
```
### Multiple measurements
- Data is split across multiple measurements like voltage, current, power_factor, etc. 
- Each has a `field` named value.
- Basic queries
```sql
-- 1. List all measurements
SHOW MEASUREMENTS

-- 2. See field keys (should show 'value')
SHOW FIELD KEYS FROM voltage
SHOW FIELD KEYS FROM current

-- 3. Sample voltage records
SELECT * FROM voltage LIMIT 10

-- 4. See all meters for 'current'
SHOW TAG VALUES FROM current WITH KEY = "meter_id"
```
- Analytical queries
```sql
-- 1. Average voltage per meter
SELECT MEAN(value) FROM voltage GROUP BY meter_id

-- 2. Current trend for a specific meter
SELECT value FROM current WHERE meter_id = 'METER-001' AND time > now() - 6h

-- 3. Compare average voltage and current (overlay in dashboard)
SELECT MEAN(value) FROM voltage WHERE time > now() - 1d;
SELECT MEAN(value) FROM current WHERE time > now() - 1d;

-- 4. Reactive power per phase
SELECT MAX(value) FROM reactive_power GROUP BY phase
```
