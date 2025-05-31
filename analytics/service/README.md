# Data ingestion service 
## Service design
- **Subscribes to**: ActiveMQ queue with hourly meter data (all OBIS codes in one message).  
- **InfluxDB schema**:  
  - Measurement: `power_quality`  
  - Tags: `meter_id`  
  - Fields: `voltage_a, voltage_b, voltage_c, current_a, current_b, current_c, power_factor, frequency`  
  - Timestamp: provided by sensor (every 15 mins)  
- **Data handling**:  
  - One point per message (all fields together).  
  - No retry on insert failure; log and drop messages.  
  - Batch size configurable; default 1 (hourly data).  
  - No time-based batch flush needed due to data cadence.  
- **Performance**:  
  - Designed for burst loads (e.g., 500k meters pushing near-simultaneously).  
  - Autoscaling based on CPU usage and queue length.  
- **Architecture**:  
  - Stateless service for easy horizontal scaling.  
  - Metrics and health checks planned for future.