# Timeseries 
- [InfluxDB setup](setup/README.md)
## Schema 
- Database/Measurement_Name: power_quality
- Fields
  - `voltage_a`, `voltage_b`, `voltage_c`
  - `current_a`, `current_b`, `current_c`
  - `active_power`, `reactive_power`
  - `power_factor`
  - `frequency` 
- Timestamp
  - Provided by sensor (every 15 minutes)
  - Stored in nanoseconds

#### 5. Sample Line Protocol
```text
power_quality,meter_id=MTR001 voltage_a=230.1,voltage_b=228.7,voltage_c=225.4,current_a=10.2,current_b=9.8,current_c=11.0,active_power=650.2,reactive_power=120.5,power_factor=0.92,frequency=50.0 1717140000000000000
```