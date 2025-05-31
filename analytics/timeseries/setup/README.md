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
## Script 
-This will generate 300 lines (5 meters × 3 phases × 5 metrics × 4 intervals).
- You can redirect the output to a .txt file and write it into InfluxDB via CLI or HTTP API.
```python
import random
from datetime import datetime, timedelta

meters = [f"DLMS00{i}" for i in range(1, 6)]
start_time = datetime.utcnow()
interval = timedelta(minutes=15)

phases = ['A', 'B', 'C']
obis_codes = {
    'voltage': {'A': '1.0.32.7.0.255', 'B': '1.0.52.7.0.255', 'C': '1.0.72.7.0.255'},
    'current': {'A': '1.0.31.7.0.255', 'B': '1.0.51.7.0.255', 'C': '1.0.71.7.0.255'},
    'active_power': {'all': '1.0.1.7.0.255'},
    'reactive_power': {'all': '1.0.3.7.0.255'},
    'power_factor': {'all': '1.0.13.7.0.255'},
}

def generate_line(measurement, obis, meter_id, phase, value, timestamp):
    ts_ns = int(timestamp.timestamp() * 1e9)
    return f"{measurement},obis_code={obis},phase={phase},meter_id={meter_id} value={value} {ts_ns}"

# simulate one hour of data (4 intervals)
for i in range(4):
    ts = start_time + i * interval
    for meter in meters:
        for phase in phases:
            print(generate_line("voltage", obis_codes['voltage'][phase], meter, phase, round(random.uniform(220, 240), 1), ts))
            print(generate_line("current", obis_codes['current'][phase], meter, phase, round(random.uniform(4.5, 6.0), 2), ts))
            print(generate_line("active_power", obis_codes['active_power']['all'], meter, phase, round(random.uniform(1000, 1500), 2), ts))
            print(generate_line("reactive_power", obis_codes['reactive_power']['all'], meter, phase, round(random.uniform(300, 500), 2), ts))
            print(generate_line("power_factor", obis_codes['power_factor']['all'], meter, phase, round(random.uniform(0.9, 1.0), 3), ts))
```