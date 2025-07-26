# Analytics on timeseries data
- [Solving grid problems using power quality data](#solving-grid-problems-using-power-quality-data)
- [Event collection](#event-collection)
- [Data source](#data-source)
- [Ingestion flow](#ingestion-flow)
- [Storage model](#storage-model)
- [Timeseries DB Vs Datalake](#timeseries-db-vs-datalake)
## [Solving grid problems using power quality data](https://github.com/sbhrwl/energy/blob/main/meteringdata/PQadapter/README.md)
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

## Data source
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
- Consider
  - 500,000 meters
  - ~10 OBIS codes per meter
  - Data pushed hourly  
    - 24 hourly pushes per day
### Store meter wise data
- 500k meters × 24 pushes/day = **12 million records/day**
### Split each OBIS reading into its own record
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
- (500,000 × 10 × 24) = **120 million records/day**
## Timeseries DB Vs Datalake
- [Timeseries DB](https://github.com/sbhrwl/system_design/blob/main/docs/datastores/timeseries/README.md)
- [Datalake](datalake/README.md)

| Aspect | InfluxDB Time Series DB                    | GCS Data Lake |
|--------|--------------|------------|
| **Data type**           | Time series with schema (measurements, tags, fields) | Raw files (JSON, CSV, Parquet, etc.)      |
| **Schema flexibility**  | Schema-on-write, structured for time series | Fully flexible (schema-on-read)            |
| **Query type**          | Real-time queries, aggregations, analytics | Batch queries (e.g., BigQuery, Spark)      |
| **Latency**             | Low latency (seconds to sub-seconds)       | Higher (minutes to hours)                  |
| **Scalability**         | High but limited by cluster size            | Virtually unlimited (object storage)       |
| **Cost**                | Compute + storage costs, usually higher     | Low storage cost, pay per access            |
| **Data ingestion**      | Optimized for high-frequency inserts        | Batch/streaming, can store raw and enriched |
| **Durability & backup** | Built-in retention policies, backups possible | Built-in durable storage, easy backups      |
| **Flexibility for analytics** | Limited to time-series analytics within DB | Supports complex analytics with external tools |
| **Cloud vendor lock-in**| Some lock-in due to DB engine and query language | Moderate (can migrate files but tooling may vary) |
| **Setup complexity**    | Medium (DB setup, schema design, maintenance) | Simple (upload files), but need query infra |
| **Use case fit**        | Real-time monitoring, anomaly detection     | Long-term storage, historical analytics    |