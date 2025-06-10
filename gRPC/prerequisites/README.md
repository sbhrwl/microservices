# Sensor registration
- [ActiveMQ setup](#activemq-setup)
- [PostgreSQL setup](#postgresql-setup)
## ActiveMQ setup
- Create a [`docker-compose.yml`](activemq/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- ActiveMQ console
  - http://localhost:8161 - admin/admin
## PostgreSQL setup
- Create a [`docker-compose.yml`](postgresql/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Download Influxdb shell: https://dl.influxdata.com/influxdb/releases/influxdb-1.8.10_windows_amd64.zip
- unzip and double click `influx.exe`
```
Connected to http://localhost:8086 version 1.8.10
InfluxDB shell version: 1.8.10
> SHOW DATABASES
name: databases
name
----
_internal
> CREATE DATABASE power_quality
> USE power_quality
Using database power_quality
> SHOW DATABASES
name: databases
name
----
_internal
power_quality
> INSERT voltage,meter_id=meter001,phase=A,obis_code=1.0.32.7.0.255 value=229.5
> SELECT * FROM voltage
name: voltage
time                meter_id obis_code      phase value
----                -------- ---------      ----- -----
1748698153029208483 meter001 1.0.32.7.0.255 A     229.5
```
