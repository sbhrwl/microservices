# Data Lake Structure in GCS

- [Objective](#objective)
- [Transformation options](#transformation-options)
  - [Time only](#time-only)
  - [Time with meter id](#time-with-meter-id)
  - [Time with OBIS code](#time-with-obis-code)
- [Mitigating large number of files](#mitigating-large-number-of-files)
- [Storage access efficiency](#storage-access-efficiency)
- [GCS directory structure options](#gcs-directory-structure-options)
  - [Reading type followed by timestamp](#reading-type-followed-by-timestamp)
  - [Hive compatible folder structure](#hive-compatible-folder-structure)
- [BigQuery external table setup](#bigquery-external-table-setup)
- [Query options](#query-options)
- [Summary](#summary)

## Objective
- Store parsed DLMS smart meter data efficiently for periodic analytics, keeping it cloud-agnostic and query-ready.

## Transformation options
### Time only
- [Time Only](#time-only)
- All meters in one file per hour.
- 🧩 Simplest structure.
- ⚠️ Harder to isolate individual meters.
- 🚀 Fast for global batch queries.
- Example path: `gs://pq-data/{year}/{month}/{day}/{hour}/all-meters.json`

### Time with meter id
- Easy time-based filtering.
- Can load data for specific meters.
- Example path: `gs://pq-data/{year}/{month}/{day}/{hour}/meter-{meterId}.json`
- File count estimate:
  - **500k meters × 24 pushes/day = 12 million files/day**.

### Time with OBIS code
- Optimized for analytical queries by metric type.
- ⚠️ More complexity in ingestion.
- ⚠️ Spreads one meter's data across multiple files.
- Example path: `gs://pq-data/{year}/{month}/{day}/{hour}/obis-{code}.json`
- File count estimate:
  - **500k meters × 10 OBIS codes × 24 pushes/day = 120 million OBIS readings/day**.

## Mitigating large number of files
- **Batch** by group of meters or obis readings:
  - If we batch `500 meters` per file:
    - 12 million ÷ 500 = 24,000 files/day.
  - If we batch `5,000 obis readings` per file:
    - 120 million ÷ 5,000 = 24,000 files/day.
- Use GCS compose or **Dataflow job** to consolidate small files hourly or daily.

## Storage access efficiency
- Time + Meter vs Time + OBIS Code comparison:

| Criteria                        | Time + Meter                             | Time + OBIS Code                            |
|--------------------------------|------------------------------------------|---------------------------------------------|
| **Optimized for**             | Per-meter diagnostics / traceability     | Analytics by OBIS (e.g., voltage trends)    |
| **Reads for bulk analytics**  | ❌ Less efficient (need to scan all meters) | ✅ Efficient (scan one OBIS type)           |
| **Reads for single meter**    | ✅ Fast (data is in one place per meter)  | ❌ Slower (need to search all OBIS files)   |
| **Write complexity**          | Low (one file per push)                  | Medium (grouping by OBIS)                   |
| **File count (raw)**          | 12M/day (or 24k/day if batched)          | 24,000/day (with 5k OBIS readings/file)     |
| **Best for**                  | Auditing, debugging per device           | Aggregation, trend detection                |

## GCS directory structure options

### Reading type followed by timestamp

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

### Hive compatible folder structure

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

## BigQuery external table setup

- Example DDL for Harmonics:
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

## Query options

| Use case  | Best option |
|-----------|-------------|
| One-time or ad hoc analysis      | BigQuery external table      |
| Frequent queries & speed is key  | Load into BigQuery native    |
| Complex ETL or ML workflows      | Dataproc (Spark/PySpark)     |
| Lightweight script/local dev     | Pandas or DuckDB with GCSFS  |

## Summary
- Choose **Time + Meter** if:
  - Your focus is **per-device investigation**, e.g., troubleshooting a faulty meter.
  - Simpler writing logic, no need to group by OBIS.
- Choose **Time + OBIS Code** if:
  - Main use case is **analytics across all meters**.
  - You want to quickly analyze e.g., voltage dips, sags, across regions.
