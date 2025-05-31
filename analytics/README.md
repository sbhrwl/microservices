# Analytics on power quality data
- [Grid problems to solve using power quality data](grid-problems-to-solve-using-power-quality-data)
- [Event collection](#event-collection)
- [Data source or initial discovery](data-source-or-initial-discovery)
- [Ingestion flow](#ingestion-flow)
- [Storage model](#storage-model)
- [Datalake Vs Timeseries DB](#datalake-vs-timeseries-db)
- [Data ingestion service](service/README.md)
## [Grid problems to solve using power quality data](https://github.com/sbhrwl/energy/blob/main/meteringdata/PQadapter/README.md)
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
- Each push profile includes **individual OBIS codes** and corresponding values for:
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

- **DLMS smart meter**
  - Sends hourly push of power quality measurements via DLMS push profile.
- **DLMS gateway**
  - Parses incoming DLMS raw data.
  - Converts into structured JSON format.
  - Pushes JSON to **Queue-Analytics**.
- **Ingestion start point**
  - Subscribe to **Queue-Analytics** and consume parsed data:
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
## Storage model
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
- ~120 million records/day
  - Assuming:
    - 500,000 meters
    - ~10 OBIS codes per meter
    - 24 hourly pushes per day
    - ➡️ That’s 120 million records/day
(500,000 × 10 × 24)
- Data pushed hourly  
- Need to support filtering by time, meterId, and obiscode 
- [Datalake GCS](datalake/README.md)
- [Timeseries DB](timeseries/README.md)

## Datalake Vs Timeseries DB 

| Aspect                 | GCS Data Lake                              | InfluxDB Time Series DB                    |
|------------------------|-------------------------------------------|--------------------------------------------|
| **Data type**           | Raw files (JSON, CSV, Parquet, etc.)      | Time series with schema (measurements, tags, fields) |
| **Schema flexibility**  | Fully flexible (schema-on-read)            | Schema-on-write, structured for time series |
| **Query type**          | Batch queries (e.g., BigQuery, Spark)      | Real-time queries, aggregations, analytics |
| **Latency**             | Higher (minutes to hours)                   | Low latency (seconds to sub-seconds)       |
| **Scalability**         | Virtually unlimited (object storage)       | High but limited by cluster size            |
| **Cost**                | Low storage cost, pay per access            | Compute + storage costs, usually higher     |
| **Data ingestion**      | Batch/streaming, can store raw and enriched | Optimized for high-frequency inserts        |
| **Durability & backup** | Built-in durable storage, easy backups      | Built-in retention policies, backups possible |
| **Flexibility for analytics** | Supports complex analytics with external tools | Limited to time-series analytics within DB |
| **Cloud vendor lock-in**| Moderate (can migrate files but tooling may vary) | Some lock-in due to DB engine and query language |
| **Setup complexity**    | Simple (upload files), but need query infra | Medium (DB setup, schema design, maintenance) |
| **Use case fit**        | Long-term storage, historical analytics    | Real-time monitoring, anomaly detection     |