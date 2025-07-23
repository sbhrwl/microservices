# Microservice design
- [Purpose](#purpose)
- [Architecture overview](#architecture-overview)
- [Microservice responsibilities](#microservice-responsibilities)
- [File structure in GCS](#file-structure-in-gcs)
  - [File format](#file-format)
  - [File path Convention](#file-path-convention)
  - [Metadata](#metadata)
  - [Manifest or index files](#manifest-or-index-files)
- [External table setup](#external-table-setup)
- [Access control](#access-control)
- [Data delivery expectations](#data-delivery-expectations)
- [Reliability and monitoring](#reliability-and-monitoring)
- [Future considerations](#future-considerations)
- [Summary](#summary)
- [Options for GCS directory tree](#options-for-gcs-directory-tree)
  - [Reading Type Followed by Timestamp](#reading-type-followed-by-timestamp)
  - [Hive Compatible Folder Structure](#hive-compatible-folder-structure)
  - [Query Options](#query-options)
## Purpose
- This microservice handles ingestion of 10-minute profile data from field sensors and makes it accessible to the analytics team through GCS and BigQuery external tables.
- Responsibilities include:
  - Collect raw 10-minute data from sensors (via ActiveMQ).
  - Convert it into analytics-friendly Parquet format.
  - Upload files to Google Cloud Storage (GCS).
  - Expose data via BigQuery external tables for analytics applications.

## Architecture overview
```
[ActiveMQ] --> [Ingestion Microservice]
[Ingestion Microservice] --> [Parquet File Generation]
[Parquet File Generation] --> [GCS Upload]
[GCS Upload] --> [BigQuery External Tables]
[BigQuery External Tables] --> [Analytics Application (Pull-Based)]
```

## Microservice responsibilities
* Consume messages from **ActiveMQ**.
* Perform **protocol conversion** if required.
* Generate **Parquet files** containing:
  * Data from multiple registers for each sensor.
  * Full snapshot every 10 minutes.
* Upload files to **GCS**, including `.json` metadata files.
* Handle upload **retries and errors** gracefully.

## File structure in GCS
### File Format
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

## External table setup
* BigQuery uses **external tables** to directly query GCS-stored Parquet files.
* No ingestion into native BigQuery tables — this keeps costs low and storage decoupled.
* Schema evolution is handled on the Parquet side.

## Access control
* If the 3rd party is trusted and the data schema is stable, give them direct read-only BigQuery access via IAM roles (e.g., `roles/bigquery.dataViewer`).
  * Analytics vendor accesses BigQuery external tables.
  * IAM roles assigned to vendor to allow **read-only access**.
* If tighter security or complex query needs are required, build a dedicated API layer (e.g., a microservice or Cloud Run app) that queries BigQuery and exposes only required data.

## Data delivery expectations
| Metric                | Value                         |
| --------------------- | ----------------------------- |
| Data frequency        | Every 10 minutes              |
| File delivery batch   | Every 30 minutes (pull-based) |
| Estimated devices     | 100,000                       |
| Registers per device  | 20 (planned growth to 30–40)  |
| Data volume per batch | ~200M records per 30 mins     |
| Data format           | Parquet                       |
| Storage               | Google Cloud Storage (GCS)    |
| Query interface       | BigQuery (External Table)     |

## Reliability and monitoring
* Upload failures handled with **automatic retries**.
* Basic logging to service logs; centralized observability can be added later.
* No upload status posted back to ActiveMQ queue.

## Future considerations
* Introduce **schema versioning** if backward-incompatible changes arise.
* Explore **delta filtering** if bandwidth/storage costs grow.
* Consider **partitioning register groups** for better parallelism in analytics.
* Add **metrics, dashboards**, and alerting for production observability.

## Summary
- This setup balances **storage scalability**, **analytics compatibility**, and **operational simplicity**.
- It leverages GCS and BigQuery efficiently, enabling the analytics team to consume fresh data with minimal infrastructure friction.

## Options for GCS directory tree

### Reading Type Followed by Timestamp
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

### Hive Compatible Folder Structure
```
gs://<bucket-name>/meter-data/
├── interval/
│   ├── 0.0.1.0.0.1.54.2.1.0.0.0.0.0.128.0.29.0/      ← Harmonics
│   │   └── year=2025/month=06/day=18/hour=08/minute=00/
│   │       └── part-0001.parquet
│   └── 0.7.0.0.0.1.0.0.0.0.0.0.0.0.128.0.27.0/      ← UnderVoltage
│       └── year=2025/month=06/day=18/hour=08/minute=00/
│           └── part-0001.parquet
```

- Matching external Table DDL (Example: Harmonics)
```sql
CREATE OR REPLACE EXTERNAL TABLE `my_dataset.harmonics`
OPTIONS (
  format = 'PARQUET',
  uris = ['gs://<bucket-name>/meter-data/interval/0.0.1.0.0.1.54.2.1.0.0.0.0.0.128.0.29.0/*'],
  hive_partitioning_mode = 'AUTO',
  hive_partitioning_source_uri_prefix = 'gs://<bucket-name>/meter-data/interval/0.0.1.0.0.1.54.2.1.0.0.0.0.0.128.0.29.0/',
  require_hive_partition_filter = TRUE
);
```

- Benefits:
  * Fast filtering by `year`, `month`, etc.
  * No need to define schema manually if Parquet includes it.
  * Works without loading data into BigQuery storage.

### Query options 
| Use case  | Best option |
|-----------|-------------|
| One-time or ad hoc analysis      | BigQuery external table      |
| Frequent queries & speed is key  | Load into BigQuery native    |
| Complex ETL or ML workflows      | Dataproc (Spark/PySpark)     |
| Lightweight script/local dev     | Pandas or DuckDB with GCSFS  |
