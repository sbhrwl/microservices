# Meter data ingestion and delivery pipeline
- This microservice handles ingestion of 10-minute profile data from field sensors and makes it accessible to the analytics team through GCS and BigQuery external tables.
## 📌 Purpose
- Collect raw 10-minute data from sensors (via ActiveMQ).
- Convert it into analytics-friendly Parquet format.
- Upload files to Google Cloud Storage (GCS).
- Expose data via BigQuery external tables for analytics applications.

## 🧩 Architecture overview
```
[ActiveMQ] --> [Ingestion Microservice]
[Ingestion Microservice] --> [Parquet File Generation]
[Parquet File Generation] --> [GCS Upload]
[GCS Upload] --> [BigQuery External Tables]
[BigQuery External Tables] --> [Analytics Application (Pull-Based)]
````

## 🛠️ Microservice responsibilities
* Consume messages from **ActiveMQ**.
* Perform **protocol conversion** if required.
* Generate **Parquet files** containing:
  * Data from multiple registers for each sensor.
  * Full snapshot every 10 minutes.
* Upload files to **GCS**, including `.json` metadata files.
* Handle upload **retries and errors** gracefully.

## 📂 File structure in GCS
### File format
* **Parquet** — compact, optimized for analytics.
* Files contain **all registers** every 10 minutes (no delta filtering).
### File path convention
```
/meter-data/{register}/yyyy/MM/dd/HH/mm/part-xxxx.parquet
```

### Metadata
* A `.json` file accompanies each `.parquet` file, containing:
  * `device_count`
  * `timestamp_range`
  * `register_list`
  * `generation_time`
### Manifest or index files
* Daily manifest files list all Parquet chunks for easier discovery and ingestion by the analytics team.

## ⛓️ External table setup
* BigQuery uses **external tables** to directly query GCS-stored Parquet files.
* No ingestion into native BigQuery tables — this keeps costs low and storage decoupled.
* Schema evolution is handled on the Parquet side.
## 🔐 Access Control
* Analytics vendor accesses BigQuery external tables.
* IAM roles assigned to vendor to allow **read-only access**.
* API Gateway handles access to GCS only if direct file reading becomes necessary (not required now).
## 📊 Data delivery expectations

| Metric                | Value                         |
| --------------------- | ----------------------------- |
| Data frequency        | Every 10 minutes              |
| File delivery batch   | Every 30 minutes (pull-based) |
| Estimated devices     | 100,000                       |
| Registers per device  | 20 (planned growth to 30–40)  |
| Data volume per batch | \~200M records per 30 mins    |
| Data format           | Parquet                       |
| Storage               | Google Cloud Storage (GCS)    |
| Query interface       | BigQuery (External Table)     |

## 🧪 Reliability and monitoring
* Upload failures handled with **automatic retries**.
* Basic logging to service logs; centralized observability can be added later.
* No upload status posted back to ActiveMQ queue.

## 🚧 Future Considerations
* Introduce **schema versioning** if backward-incompatible changes arise.
* Explore **delta filtering** if bandwidth/storage costs grow.
* Consider **partitioning register groups** for better parallelism in analytics.
* Add **metrics, dashboards**, and alerting for production observability.

## 📁 Sample GCS directory tree
```
meter-data/
├── voltage/
│   └── 2025/
│       └── 06/
│           └── 18/
│               └── 09/
│                   ├── 10/
│                   │   ├── part-0001.parquet
│                   │   ├── part-0001.json
│                   │   └── ...
│                   └── manifest-2025-06-18.json
└── ...
```

```
meter-data/
├── 0.0.1.0.0.1.54.2.1.0.0.0.0.0.128.0.29.0/   ← ReadingType (harmonics)
│   └── 2025/
│       └── 05/
│           └── 16/
│               └── 08/
│                   ├── 00/
│                   │   ├── part-0001.parquet
│                   │   ├── part-0001.json
│                   ├── 10/
│                   │   ├── part-0002.parquet
│                   │   ├── part-0002.json
│                   ├── 20/
│                   │   ├── part-0003.parquet
│                   │   ├── part-0003.json
│                   ├── 30/
│                   │   ├── part-0004.parquet
│                   │   ├── part-0004.json
│                   ├── 40/
│                   │   ├── part-0005.parquet
│                   │   ├── part-0005.json
│                   ├── 50/
│                   │   ├── part-0006.parquet
│                   │   ├── part-0006.json
│                   └── 00/
│                       ├── part-0007.parquet
│                       ├── part-0007.json
│               └── manifest-2025-05-16.json
└── ...
```

```
meter-data/
├── interval/
│   ├── 0.0.1.0.0.1.54.2.1.0.0.0.0.0.128.0.29.0/      ← Harmonics
│   │   └── yyyy/MM/dd/HH/mm/
│   │       └── part-xxxx.parquet
│   └── 0.7.0.0.0.1.0.0.0.0.0.0.0.0.128.0.27.0/      ← TimeThresholdForCriticalUnderVoltage
│       └── yyyy/MM/dd/HH/mm/
│           └── part-xxxx.parquet
```

## ✅ Summary
- This setup balances **storage scalability**, **analytics compatibility**, and **operational simplicity**.
- It leverages GCS and BigQuery efficiently, enabling the analytics team to consume fresh data with minimal infrastructure friction.
