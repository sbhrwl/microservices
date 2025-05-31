# Data ingestion service 
## Service design
- Service subscribes to queue with hourly meter data (multiple obis codes as fields).
- InfluxDB schema:
  - Measurement: `power_quality`
  - Tags: `meter_id`
  - Fields: voltage_a, voltage_b, voltage_c, current_a, current_b, current_c, power_factor, frequency
  - Timestamp: from sensor (every 15 minutes)
- Inserts one point per message (all fields together).
- No retry on insert failure; failures are logged and messages dropped.
- Batch size configurable; default can be 1 due to hourly data arrival.
- No time-based batch flush needed given hourly data cadence.
- System designed for potential burst loads (e.g., 500k meters pushing close in time).
- Autoscaling based on CPU usage and queue length.
- Service is stateless for easy horizontal scaling.
- Metrics and health checks to be added in future iterations.