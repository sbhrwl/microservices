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

- [Analysis](#analysis)
- [Solution options and decisions](#solution-options-and-decisions)
- [Summary of analysis](#summary-of-analysis)
## Analysis

| **Category**                  | **Details**                      |
| ----------------------------- | -------------------------------- |
| **Database & Storage**        | - Current DB: Oracle (no partitioning, acceptable performance) <br> - Volume: 100k meters → ~500k rows/day <br> - Data retention: 90 days (Oracle) <br> - 10-min profile: ~14.4M rows/day <br> - 10-min data retention TBD (possibly 60 days) <br> - Open to migrating 10-min data to better storage solution |
| **Data Ingestion & Pipeline** | - Pipeline: ActiveMQ → service → protocol conversion → enrichment → Oracle DB <br> - Bottlenecks observed: CPU, IO, memory under load <br> - No raw message persistence (no replay/audit support)|
| **Scalability & Performance** | - Current service is stateful and not horizontally scalable <br> - Target batch processing latency: ≤10 minutes <br> - Some tolerance for missing/late data|
| **Query & Access Patterns**   | - Initial queries: batch processing acceptable <br> - Real-time queries to be revisited later <br> - Queries pushed downstream; no current direct heavy query load|
| **Monitoring & Reliability**  | - Ingestion failure monitoring/alerting handled downstream <br> - No inbuilt observability or replay capability in ingestion flow|
| **Data Model & Growth**       | - Current sensor registers: 5 <br> - Expected growth to 15 registers <br> - Design must accommodate register growth|
| **Messaging System**          | - Messaging uses ActiveMQ <br> - Scaling strategy for registers via ActiveMQ undefined <br> - Future options: clustering, partitioning, load balancing|

## Solution options and decisions

| **Category**  | **Item** | **Decision**|
| ------------- | -------- | ----------- |
| **Storage vs. Query Needs**       | 1. Data mostly stored and passed downstream; minimal querying needs.                              | Data Lake        |
|                              | 2. Data transformed and pushed to analytics in required formats.                                  | Data Lake        |
|                              | 3. Downstream apps don’t require structured SQL-like access.                                      | Data Lake        |
|                              | 4. No enrichment or joins before handing off the data.                                            | Data Lake        |
|                              | 5. Preference to optimize for storage cost over query speed.                                      | Data Lake        |

## Summary of analysis
* Based on current needs, **Data Lake architecture** (e.g., Google Cloud Storage + Parquet + Iceberg or BigQuery) is the most suitable choice.
* Time-series DBs might be preferred if requirements shift to:
  * Frequent, low-latency queries on recent data.
  * Built-in rollups, aggregations, alerting.
  * Active dashboard use (Grafana, etc.).
  * Efficient retention and compression policies.
  * High-ingestion throughput with fast query performance.