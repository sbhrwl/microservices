# Designing data lake in GCS
- [Objective](#objective)
- [Transformation options](#transformation-options)
  - [Time only](#time-only)
  - [Time with Meter Id](#time-with-meter-id)
  - [Time with OBIS code](#time-with-obis-code)
- [Mitigating large number of files](#mitigating-large-number-of-files)
- [Storage access efficiency](#storage-access-efficiency)
- [Study](study/README.md)
- [Plan](plan/README.md)
## Objective
- Store parsed DLMS smart meter data efficiently for periodic analytics, keeping it cloud-agnostic and query-ready.
## Transformation options
### Time only 
- All meters in one file per hour
- 🧩 Simplest structure  
- ⚠️ Harder to isolate individual meters  
- 🚀 Fast for global batch queries  
- `gs://pq-data/{year}/{month}/{day}/{hour}/all-meters.json`
### Time with Meter Id
- Easy time-based filtering  
- Can load data for specific meters  
- `gs://pq-data/{year}/{month}/{day}/{hour}/meter-{meterId}.json`
- File count estimate
  - **500k meters × 24 pushes/day = `12 million files/day`**
### Time with OBIS code
- Optimized for analytical queries by metric type  
- ⚠️ More complexity in ingestion  
- ⚠️ Spreads one meter's data across multiple files
- `gs://pq-data/{year}/{month}/{day}/{hour}/obis-{code}.json`
- File count estimate
  - **500k meters × 10 OBIS codes × 24 pushes/day = `120 million OBIS readings/day`**

## Mitigating large number of files
- **Batch** by group of meters or obis readings
  - If we batch `500 meters` per file:
    - 12 million ÷ 500 = 24,000 files/day
  - If we batch `5,000 obis readings` per file:
    - 120 million ÷ 5,000 = 24,000 files/day
- Use GCS compose or **Dataflow job** to consolidate small files hourly or daily.

## Storage access efficiency
- Time + Meter vs Time + OBIS Code

| Criteria                        | Time + Meter                             | Time + OBIS Code                            |
|-------------------------------|------------------------------------------|---------------------------------------------|
| **Optimized for**             | Per-meter diagnostics / traceability     | Analytics by OBIS (e.g., voltage trends)    |
| **Reads for bulk analytics** | ❌ Less efficient (need to scan all meters) | ✅ Efficient (scan one OBIS type)           |
| **Reads for single meter**    | ✅ Fast (data is in one place per meter)  | ❌ Slower (need to search all OBIS files)   |
| **Write complexity**          | Low (one file per push)                  | Medium (grouping by OBIS)                   |
| **File count (raw)**          | 12M/day (or 24k/day if batched)          | 24,000/day (with 5k OBIS readings/file)     |
| **Best for**                  | Auditing, debugging per device           | Aggregation, trend detection                |

### Summary
- Choose **Time + Meter** if:
  - Your focus is **per-device investigation**, e.g., troubleshooting a faulty meter
  - Simpler writing logic, no need to group by OBIS
- Choose **Time + OBIS Code** if:
  - Main use case is **analytics across all meters**
  - You want to quickly analyze e.g., voltage dips, sags, across regions