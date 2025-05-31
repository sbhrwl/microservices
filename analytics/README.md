# [Analytics on power quality data](https://github.com/sbhrwl/energy/blob/main/meteringdata/PQadapter/README.md)
- [Event collection](#event-collection)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Grid problems to solve using power quality data](grid-problems-to-solve-using-power-quality-data)
- [Data source or initial discovery](data-source-or-initial-discovery)
- [Ingestion flow](#ingestion-flow)
## Grid problems to solve using power quality data
- **Phase imbalance**
- **Voltage fluctuations and sags**
- **Harmonics and distortion**
## Event collection 

| Measurement            | Phase imbalance | Voltage fluctuations and sags | Harmonics and distortion |
|------------------------|------------------|------------------------------|-------------------------|
| Phase (A/B/C)          | ✅               | ✅                           |                         |
| Phase Voltage          | ✅               | ✅                           | ✅                      |
| Phase Current          | ✅               |                              | ✅                      |
| Power Factor           |                  |                              | ✅                      |
| Power Quality (THD, dips, swells) |        | ✅                           | ✅                      |

## Data source or initial discovery
- Data is collected from **DLMS smart meters** aka **sensors**
- Smart meters **push power quality data hourly** using a **push profile** configured in a **push schedular**
- Each push profile includes **individual OBIS codes** for:
  - Phase (A/B/C) 
  - Phase voltage
  - Phase current
  - Power factor
  - Power quality metrics (e.g., THD, sags/swells)
## Ingestion flow
- DLMS smart meters push data to the **Gateway**
- **All meter data arrives on a single queue**, regardless of:
  - Meter ID
  - OBIS code
```
DLMS smart meter
   │
   ▼
DLMS Gateway (parses DLMS → JSON)
   │
   ▼
Queue-Analytics (JSON messages)
   │
   ▼
Power quality ingestion service (to be built)
   │
   ▼
[Target Storage Layer – TBD]
   ▼
(Available for analytics)
```

1. **DLMS Smart Meter**
   - Sends hourly push of power quality measurements via DLMS push profile.
2. **DLMS Gateway**
   - Parses incoming DLMS raw data.
   - Converts into structured JSON format.
   - Pushes JSON to an **internal queue (Queue B)**.
3. **Ingestion Start Point**
   - We subscribe to **Queue-Analytics** with structured data:
     ```json
     {
       "meterId": "DLMS123456",
       "timestamp": "2025-05-31T10:00:00Z",
       "values": [
         { "obis": "1.0.32.7.0.255", "value": 230.1 },
         { "obis": "1.0.52.7.0.255", "value": 231.0 },
         ...
       ]
     }
     ```
## Storage Model
- We will **split each OBIS reading** into its own record.
- Minimal fields per record:
  ```json
  {
  "meterId": "DLMS123456",
  "timestamp": "2025-05-31T10:00:00Z",
  "obis": "1.0.32.7.0.255",
  "value": 230.1,
  "measurementType": "voltage",
  "phase": "A",
  "unit": "V"
  }
  ```

