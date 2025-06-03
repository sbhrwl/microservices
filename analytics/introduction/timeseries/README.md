# Timeseries
- [Schema](#schema)
- [Sample line protocol](#sample-line-protocol)
- [What Is a Timeseries database](#what-is-a-timeseries-database)
- [Data model in InfluxDB](#data-model-in-influxdb)
- [Writing data](#writing-data)
  - [One measurement style](#one-measurement-style)
  - [Multiple measurement style](#multiple-measurement-style)
- [Querying data](#querying-data)
  - [Show measurements](#show-measurements)
  - [List series](#list-series)
  - [Basic select](#basic-select)
  - [Filter by tags](#filter-by-tags)
- [Analytical queries](#analytical-queries)
  - [Daily average voltage](#daily-average-voltage)
  - [Compare voltages between phases](#compare-voltages-between-phases)
  - [Detect voltage drop](#detect-voltage-drop)
- [Useful admin commands](#useful-admin-commands)
  - [Show tag keys](#show-tag-keys)
  - [Show field keys](#show-field-keys)
- [Tools](#tools)
- [Summary](#summary)
## Schema 
- Database/Measurement_Name: power_quality
- Fields
  - `voltage_a`, `voltage_b`, `voltage_c`
  - `current_a`, `current_b`, `current_c`
  - `power_factor`
  - `frequency` 
- Timestamp
  - Provided by sensor (every 15 minutes)
  - Stored in nanoseconds

## Sample line protocol
```text
power_quality,meter_id=MTR001 voltage_a=230.1,voltage_b=228.7,voltage_c=225.4,current_a=10.2,current_b=9.8,current_c=11.0,power_factor=0.92,frequency=50.0 1717140000000000000
```

##  What is a Timeseries database
- A time series database (TSDB) like **InfluxDB** is optimized to store and query data that is indexed by time
- Perfect for sensor data such as smart meter readings.
## Data model in InfluxDB
- InfluxDB organizes data as follows:
  - **Database**: Like a schema. Example: `power_quality`
  - **Measurement**: Like a table. Example: `power_quality`
  - **Tags**: Indexed key-value pairs for filtering. Example: `meter_id`, `phase`, `obis_code`
  - **Fields**: Actual data values. Example: `voltage`, `current`, `active_power`
  - **Timestamp**: Each data point has a time.
## Writing data
### One measurement style
* Measurement: `power_quality`
* Tags: `meter_id`, `phase`
* Fields: `voltage`, `current`, `active_power`
* Timestamp: in nanoseconds
```text
power_quality,meter_id=001,phase=L1 voltage=231.2,current=5.2,active_power=1200 1717305600000000000
````
### Multiple measurement style
- In this style, each **type of measurement** (e.g., `voltage`, `current`, `active_power`) is written as a **separate measurement** in InfluxDB.
```text
voltage,meter_id=001,phase=L1,obis_code=1-0:32.7.0 value=231.2 1717305600000000000
current,meter_id=001,phase=L1,obis_code=1-0:31.7.0 value=5.2 1717305600000000000
active_power,meter_id=001,phase=L1,obis_code=1-0:21.7.0 value=1200 1717305600000000000
```
## Querying data
### Show measurements
```sql
SHOW MEASUREMENTS
```
### List series
```sql
SHOW SERIES FROM "power_quality"
```
### Basic select
```sql
SELECT * FROM power_quality WHERE time > now() - 1h
```
### Filter by tags
```sql
SELECT voltage FROM power_quality 
WHERE meter_id='001' AND phase='L1' AND time > now() - 1h
```
## Analytical queries
### Daily average voltage
```sql
SELECT MEAN(voltage) FROM power_quality 
WHERE time > now() - 7d 
GROUP BY time(1d)
```
### Compare voltages between phases
```sql
SELECT MEAN(voltage) FROM power_quality 
WHERE meter_id='001' AND time > now() - 1d 
GROUP BY time(1h), phase
```
### Detect voltage drop
- `Alert condition`

```sql
SELECT * FROM power_quality 
WHERE voltage < 210 AND time > now() - 1h
```

## Useful admin commands
### Show tag keys
```sql
SHOW TAG KEYS FROM power_quality
```
### Show field keys
```sql
SHOW FIELD KEYS FROM power_quality
```
## Tools
* Use **Chronograf** or **Grafana** to build dashboards.
* InfluxDB supports **REST APIs** for programmatic access.
## Summary
- With InfluxDB, you can:
  * Store time-stamped power quality data efficiently
  * Query historical and real-time insights
  * Detect anomalies and visualize trends in Grafana
