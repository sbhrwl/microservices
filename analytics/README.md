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

## 📂 Designing data lake in GCS

### Objective
Store parsed DLMS smart meter data efficiently for periodic analytics, keeping it cloud-agnostic and query-ready.

### Considerations
- ~120 million records/day  
- Data pushed hourly  
- Need to support filtering by time, meterId, and possibly region or source

---

### ✅ Option 1: Time + Meter ID
- 🔍 Easy time-based filtering  
- 🔍 Can load data for specific meters  
- ⚠️ Too many small files (if each meter is separate)  
- `gs://pq-data/{year}/{month}/{day}/{hour}/meter-{meterId}.json`

### ✅ Option 2: Time + Region (batch of meters)
- 🔍 Aggregates data into fewer files  
- 🧪 Requires region-to-meter mapping  
- 📈 Good for analytics at zone/substation level  
- `gs://pq-data/{year}/{month}/{day}/{hour}/region-{regionId}.json`

### ✅ Option 3: Time only (all meters in one file per hour)
- 🧩 Simplest structure  
- ⚠️ Harder to isolate individual meters  
- 🚀 Fast for global batch queries  
- `gs://pq-data/{year}/{month}/{day}/{hour}/all-meters.json`

### ✅ Option 4: Time + OBIS Code
- 📊 Optimized for analytical queries by metric type  
- ⚠️ More complexity in ingestion  
- ⚠️ Spreads one meter's data across multiple files
- `gs://pq-data/{year}/{month}/{day}/{hour}/obis-{code}.json`