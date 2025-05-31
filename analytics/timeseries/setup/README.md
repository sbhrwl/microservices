- `docker-compose up -d`
```
version: '3'
services:
  influxdb:
    image: influxdb:1.8
    container_name: influxdb
    ports:
      - "8086:8086"
    volumes:
      - influxdb-storage:/var/lib/influxdb
    environment:
      - INFLUXDB_DB=power_quality
      - INFLUXDB_ADMIN_USER=admin
      - INFLUXDB_ADMIN_PASSWORD=admin123

volumes:
  influxdb-storage:
```

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
